package commandManager.commands;

import logger.ServerLogger;
import models.handlers.OrganizationIDHandler;
import models.handlers.PartNumberHandler;
import models.handlers.ProductHandler;
import models.handlers.ProductIDHandler;
import products.*;
import responses.CommandStatusResponse;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Objects;
import java.util.Random;

public class GeneratorCommand implements BaseCommand{
    private static Random random = new Random();


    private CommandStatusResponse response;

    @Override
    public String getName() {
        return "generate";
    }

    @Override
    public String getDescr() {
        return "Генерирует случайные продукты.";
    }

    @Override
    public String getArgs() {
        return "number [1,100]";
    }

    @Override
    public void execute(String[] args) {
        if (args.length>2){
            throw new IllegalArgumentException("Команда " + getName()+ " имеет только один аргумент.");
        }




        if (Integer.parseInt(args[1])<0 || Integer.parseInt(args[1])>100 ){
            response = CommandStatusResponse.ofString("Количество генерируемых объектов не  может быть вне диапазона [1,100].");
        } else {
            for (int i = 0; i < Integer.parseInt(args[1]); i++){
                ProductHandler.getInstance().addElementToCollection(generateProduct());

            }
            response = CommandStatusResponse.ofString("Объекты сгенерированы.");
        }

    }

    @Override
    public CommandStatusResponse getResponse() {
        return response;
    }


    private Integer genIdP(){
        return ProductIDHandler.generateId();
    }

    private  String generateRandomName() {
        return "Product_" + random.nextInt(1000);
    }

    private Coordinates generateRandomCoordinates() {
        Float x = random.nextFloat() * 100;
        int y = random.nextInt(200);
        return new Coordinates(x, y);
    }


    private  Integer generateRandomPrice() {
        return random.nextInt(1000) + 1; // Генерация цены > 0
    }


    private  String generateUniquePartNumber() {

        if (random.nextBoolean()) { // 50% шанс, что partNumber будет null
            return null;
        }
        String partNumber;



        do {
            int length = 20 + random.nextInt(50); // Длина от 20 до 69 символов
            partNumber = random.ints(48, 122) // Генерация символов от '0' до 'z'
                    .filter(i -> Character.isLetterOrDigit(i)) // Оставляем только цифры и буквы
                    .limit(length)
                    .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                    .toString();


        } while (PartNumberHandler.checkPN(partNumber));
        return partNumber;
    }


    private  Float generateRandomManufactureCost() {
        return random.nextBoolean() ? random.nextFloat() * 100 : null;
    }

    private UnitOfMeasure generateRandomUnitOfMeasure() {
        return random.nextBoolean() ? UnitOfMeasure.values()[random.nextInt(UnitOfMeasure.values().length)] : null;
    }

    private Organization generateRandomOrganization() {
        Long id = OrganizationIDHandler.generateId();
        String name = "Org-" + random.nextInt(1000);
        long employeesCount = random.nextInt(1000) + 1; // Должно быть > 0
        OrganizationType type = random.nextBoolean() ? OrganizationType.values()[random.nextInt(OrganizationType.values().length)] : null;
        Address address = random.nextBoolean() ? generateRandomAddress() : null;

        return new Organization(id, name, employeesCount, type, address);
    }

    private  Address generateRandomAddress() {
        String street = random.nextBoolean() ? "Street-" + random.nextInt(1000) : null;
        String zipCode = random.ints(48, 57) // Генерация цифр для zipCode
                .limit(5 + random.nextInt(10)) // Длина от 5 до 14 символов
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
        return new Address(street, zipCode);
    }

    public  Product generateProduct() {
        return new Product(
                genIdP(),
                generateRandomName(),
                generateRandomCoordinates(),
                LocalDate.now(), // Дата создания генерируется автоматически
                generateRandomPrice(),
                generateUniquePartNumber(),
                generateRandomManufactureCost(),
                generateRandomUnitOfMeasure(),
                generateRandomOrganization()
        );
    }







}


