package request;

import requests.AbsRequest;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;

//Этот класс отвечает за чтение запросов от клиента
public class RequestReader {
    final InputStream in;

    public RequestReader(InputStream in) {
        this.in = in;
    }

    public AbsRequest readObject() throws IOException, ClassNotFoundException {
        ObjectInputStream ois = new ObjectInputStream(in);
        return (AbsRequest) ois.readObject();
    }
}