package responses;

import java.io.Serializable;

// Абстрактный класс для всех ответов. Наследует Serializable,
// чтобы объекты могли быть сериализованы
// (что необходимо для передачи объектов между клиентом и сервером).
public abstract class AbsResponse implements Serializable {
}
