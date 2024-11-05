package response;

import exceptions.ServerNotAvailableException;
import responses.AbsResponse;
import responses.ErrorResponse;

import java.io.*;

//Этот класс отвечает за чтение ответа от сервера:
//
//ResponseReader:
//Читает объект ответа из потока InputStream, который был получен от сервера.
//Если ответ является экземпляром ErrorResponse, выбрасывается исключение ServerNotAvailableException, чтобы обработать ошибку.
//Если данные обрываются (EOFException), возвращается объект ErrorResponse.
public class ResponseReader {
    final InputStream in;

    public ResponseReader(InputStream in) {
        this.in = in;
    }

    public AbsResponse readObject() throws IOException, ClassNotFoundException, ServerNotAvailableException {
        try {
            ObjectInputStream ois = new ObjectInputStream(in);
            AbsResponse result = (AbsResponse) ois.readObject();
            if (result instanceof ErrorResponse)
                throw new ServerNotAvailableException(((ErrorResponse) result).getResponse());
            return result;
        } catch (UTFDataFormatException e ){
            return new ErrorResponse("Произошла ошибка смешения пакетов. Попробуйте послать запрос заново.");

        }

        catch (EOFException e) {

            return new ErrorResponse("Ответ от сервера не уместился в буфер. Возможно, коллекция получилась слишком большая");
        }
    }
}