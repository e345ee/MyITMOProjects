package models.handlers;

import products.Product;

import java.time.LocalDate;
import java.util.AbstractCollection;
import java.util.List;

public interface CollectionHandler<T extends AbstractCollection<E>, E> {

    void loadCollectionFromDatabase();


    //void writeCollectionToDatabase();


    T getCollection();

    void setCollection(T value);


    void addElementToCollection(E value);


    void clearCollection();



    E getFirstOrNew();


    LocalDate getInitDate();


   // void addMissingCitiesToCollection(List<Product> citiesFromDatabase);
}