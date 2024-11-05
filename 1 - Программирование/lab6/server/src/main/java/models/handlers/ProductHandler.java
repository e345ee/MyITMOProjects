package models.handlers;

import collectionStorageManager.FileManager;
import logger.ServerLogger;
import models.comparators.NameComparator;
import products.Address;
import products.Coordinates;
import products.Organization;
import products.Product;
import responses.CommandStatusResponse;
import validators.ProductValidator;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;

public class ProductHandler implements CollectionHandler<ArrayList<Product>, Product> {

    private LocalDate initDate = null;
    private CommandStatusResponse response;

    /**
     * Поле для реализации Синглтона.
     */
    private static ProductHandler singletonPattern;

    /**
     * Файл менеджер реализующий работу с файлом источником загрузки коллекции.
     */
    private FileManager<Product> fileManager;

    /**
     * {@link ArrayList} коллекция объектов {@link Product}.
     */
    private ArrayList<Product> collection;

    /**
     * Приватный конструктор для реализации Синглтона.
     */
    private ProductHandler() {
    }

    /**
     * Метод для получения {@link ProductHandler}.
     *
     * @return ссылка единственный экземпляр {@link ProductHandler}.
     */
    public static ProductHandler getInstance() {
        if (singletonPattern == null)
            singletonPattern = new ProductHandler();
        return singletonPattern;
    }

    /**
     * Устанавливает определенный объект для работы с файлами реализующий интерфейс {@link FileManager}
     *
     * @param fileManager объект для работы с чтением и записью в файл.
     */
    public void setFileManager(FileManager<Product> fileManager) {
        this.fileManager = fileManager;
    }

    /**
     * Выгружает в переменную {@link #collection} коллекцию из файла.
     *
     * @throws IOException          Если произошла ошибка в чтении файла.
     * @throws NullPointerException выбрасывается в случае если {@link #fileManager} не установлен.
     */
    public void loadCollection() throws IOException, NullPointerException {
        ProductValidator productValidator = new ProductValidator();

        if (fileManager == null) {
            throw new NullPointerException("Файл менеджер не установлен в поле класс ProductManager");
        }

        ArrayList<Product> tmpProduct = new ArrayList<>(fileManager.readFromFile());

        Iterator<Product> iterator = tmpProduct.iterator();
        while (iterator.hasNext()) {
            Product product = iterator.next();
            try {
                productValidator.validate(product);

                if (ProductIDHandler.checkId(product.getId()) || OrganizationIDHandler.checkId(product.getManufacturer().getId()) || PartNumberHandler.checkPN(product.getPartNumber())) {
                    throw new IllegalArgumentException("Уникальные поля уже есть в менеджере");
                } else {
                    ProductIDHandler.addId(product.getId());
                    OrganizationIDHandler.addId(product.getManufacturer().getId());
                    PartNumberHandler.addPN(product.getPartNumber());
                }


            } catch (IllegalArgumentException e) {
                ServerLogger.logger.error(e.getMessage());
                ServerLogger.logger.error("Удаление невалидного объекта. id: " + product.getId());
                iterator.remove();
            }
        }
        ProductHandler.getInstance().setCollection(tmpProduct);
        ProductHandler.getInstance().sortCollection();
        if (!getCollection().isEmpty()) {
            initDate = getCollection().stream().min(Comparator.comparing(Product::getCreationDate)).orElse(null).getCreationDate();
        } else {
            initDate = null;
        }
        response = CommandStatusResponse.ofString("Коллекция загружена: " + collection.size());
        ServerLogger.logger.info(response.getResponse());

    }

    public void writeCollection() {
        fileManager.writeToFile(getCollection(), "products", Product.class);
    }


    /**
     * Возвращает коллекцию продуктов.
     *
     * @return ArrayList коллекция продуктов.
     */
    @Override
    public ArrayList<Product> getCollection() {
        return collection;
    }

    /**
     * Устанавливает коллекцию.
     *
     * @param value ArrayList коллекция продуктов.
     */
    @Override
    public void setCollection(ArrayList<Product> value) {
        this.collection = value;
    }

    /**
     * Возвращает экземпляр Product.
     *
     * @return Экземпляр Product.
     */
    @Override
    public Product getFirstOrNew() {
        if (collection.iterator().hasNext())
            return collection.iterator().next();
        else
            return new Product();
    }


    @Override
    public void addElementToCollection(Product value) {
        if (collection != null) {
            collection.add(value);

        } else {
            ArrayList<Product> tmpCollection = new ArrayList<>();
            tmpCollection.add(value);
            ProductHandler.getInstance().setCollection(tmpCollection);
        }

        PartNumberHandler.addPN(value.getPartNumber());
        OrganizationIDHandler.addId(value.getManufacturer().getId());
        ProductIDHandler.addId(value.getId());
        sortCollection();
    }

    /**
     * Очищает коллекцию.
     */
    @Override
    public void clearCollection() {
        collection.clear();
    }

    /**
     * Возвращает элемент по его id.
     *
     * @param id Уникальное значение продукта.
     * @return продукт.
     */
    public Product getElementById(Integer id) {
        Product product = null;
        for (Product tmpProduct : collection) {
            if (id.equals(tmpProduct.getId())) {
                product = tmpProduct;
            }
        }
        return product;
    }


    /**
     * Клонирует объект.
     *
     * @param product Объект для клонирования.
     * @return Новый скопированный объект.
     */
    public Product cloneProduct(Product product) {
        Product newProduct = new Product();
        Coordinates newCoordinates = new Coordinates();
        Organization newOrganization = new Organization();
        Address newAddress = new Address();

        if (product.getManufacturer().getOfficialAddress() == null) {
            newAddress = null;
        } else {
            newAddress.setZipCode((product.getManufacturer().getOfficialAddress().getZipCode()));
            newAddress.setStreet((product.getManufacturer().getOfficialAddress().getStreet()));
        }

        newOrganization.setType(product.getManufacturer().getType());
        newOrganization.setOfficialAddress(newAddress);
        newOrganization.setName(product.getManufacturer().getName());
        newOrganization.setEmployeesCount(product.getManufacturer().getEmployeesCount());
        newOrganization.setId(product.getManufacturer().getId());

        newCoordinates.setX(product.getCoordinates().getX());
        newCoordinates.setY(product.getCoordinates().getY());

        newProduct.setId(product.getId());
        newProduct.setName(product.getName());
        newProduct.setCoordinates(newCoordinates);
        newProduct.setCreationDate(product.getCreationDate());
        newProduct.setPrice(product.getPrice());
        newProduct.setPartNumber(product.getPartNumber());
        newProduct.setManufactureCost(product.getManufactureCost());
        newProduct.setUnitOfMeasure(product.getUnitOfMeasure());
        newProduct.setManufacturer(newOrganization);

        return newProduct;
    }


    public void clearUniqueFields() {
        ProductIDHandler.clearIds();
        OrganizationIDHandler.clearIds();
        PartNumberHandler.clearPN();
    }

    public FileManager<Product> getFileManager() {
        return fileManager;
    }


    public void clearUniqueFieldByProduct(Product product) {
        ProductIDHandler.releaseId(product.getId());
        OrganizationIDHandler.releaseId(product.getManufacturer().getId());
        PartNumberHandler.releasePN(product.getPartNumber());
    }

    public LocalDate getInitDate() {
        return initDate;
    }

    public CommandStatusResponse getResponse() {
        return response;
    }

    public void sortCollection() {
        Collections.sort(getCollection(),
                new NameComparator());
    }

}
