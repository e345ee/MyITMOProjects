package managers;

import managers.ids.OrganizationIdManager;
import managers.ids.ProductIdManager;
import managers.validators.ProductValidator;
import products.Address;
import products.Coordinates;
import products.Organization;
import products.Product;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;


/**
 * Класс для работы с коллекцией {@link Product}.
 * Предоставляет методы для управления, загрузки и выгрузки объектов.
 * Реализует интерфейс {@link CollectionManager}.
 */
public class ProductManager implements CollectionManager<ArrayList<Product>, Product> {


    /**
     * Поле для реализации Синглтона.
     */
    private static ProductManager singletonPattern;

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
    private ProductManager() {
    }

    /**
     * Метод для получения {@link ProductManager}.
     *
     * @return ссылка единственный экземпляр {@link ProductManager}.
     */
    public static ProductManager getInstance() {
        if (singletonPattern == null)
            singletonPattern = new ProductManager();
        return singletonPattern;
    }

    /**
     * Устанавливает определенный объект для работы с файлами реализующий интерфейс {@link FileManager}
     * @param fileManager объект для работы с чтением и записью в файл.
     */
    public void setFileManager(FileManager<Product> fileManager){
        this.fileManager = fileManager;
    }

    /**
     * Выгружает в переменную {@link #collection} коллекцию из файла.
     *
     * @throws IOException Если произошла ошибка в чтении файла.
     * @throws NullPointerException выбрасывается в случае если {@link #fileManager} не установлен.
     */
    public void loadCollection() throws IOException, NullPointerException {
        ProductValidator productValidator = new ProductValidator();

        if (fileManager == null){
            throw new NullPointerException("Файл менеджер не установлен в поле класс ProductManager");
        }

        ArrayList<Product> tmpProduct = new ArrayList<>(fileManager.readFromFile());

        Iterator<Product> iterator = tmpProduct.iterator();
        while (iterator.hasNext()) {
            Product product = iterator.next();
            try {
                productValidator.validate(product);
                Product.PartNumberManager.addPN(product.getPartNumber());
                OrganizationIdManager.addId(product.getManufacturer().getId());
                ProductIdManager.addId(product.getId());
            } catch (IllegalArgumentException e){
                System.out.println(e.getMessage());
                System.out.println("Удаление невалидного объекта. id: " + product.getId());
                iterator.remove();
            }
        }

        ProductManager.getInstance().setCollection(tmpProduct);
    }

    /**
     * Записывает коллекцию в файл.
     */
    public void writeCollection(){
        fileManager.writeToFile(getCollection(), "products", Product.class);
    }


    /**
     *Возвращает коллекцию продуктов.
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
     * @return Экземпляр Product.
     */
    @Override
    public Product getFirstOrNew() {
        if (collection.iterator().hasNext())
            return collection.iterator().next();
        else
            return new Product();
    }

    /**
     * Добавляет элемент в коллекцию.
     * Также добавляет уникальные поля Product в {@link ProductIdManager}, {@link OrganizationIdManager}, {@link products.Product.PartNumberManager}.
     *
     * @param value объект Product.
     */
    @Override
    public void addElementToCollection(Product value) {
        if (collection != null){
        collection.add(value);

        }
        else {
            ArrayList<Product> tmpCollection = new ArrayList<>();
            tmpCollection.add(value);
            ProductManager.getInstance().setCollection(tmpCollection);
        }

        Product.PartNumberManager.addPN(value.getPartNumber());
        OrganizationIdManager.addId(value.getManufacturer().getId());
        ProductIdManager.addId(value.getId());
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
     * @param id Уникальное значение продукта.
     * @return продукт.
     */
    public  Product getElementById(Integer id){
        Product product = null;
        for (Product tmpProduct : collection){
            if (id.equals(tmpProduct.getId())){
                product = tmpProduct;
            }
        }
        return product;
    }


    /**
     * Клонирует объект.
     * @param product Объект для клонирования.
     * @return Новый скопированный объект.
     */
    public Product cloneProduct(Product product){
        Product newProduct = new Product();
        Coordinates newCoordinates = new Coordinates();
        Organization newOrganization = new Organization();
        Address newAddress = new Address();

        if (product.getManufacturer().getOfficialAddress() == null){
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

    /**
     * Очищает уникальные поля в {@link ProductIdManager}, {@link OrganizationIdManager}, {@link products.Product.PartNumberManager}
     */
    public void clearUniqueFields(){
        ProductIdManager.clearIds();
        OrganizationIdManager.clearIds();
        Product.PartNumberManager.clearPN();
    }

    public FileManager<Product> getFileManager() {
        return fileManager;
    }

    /**
     * Удаляет из {@link ProductIdManager}, {@link OrganizationIdManager}, {@link products.Product.PartNumberManager}
     * значения объекта.
     * @param product продукт.
     */
    public void clearUniqueFieldByProduct(Product product){
        ProductIdManager.releaseId(product.getId());
        OrganizationIdManager.releaseId(product.getManufacturer().getId());
        Product.PartNumberManager.releasePN(product.getPartNumber());
    }

}
