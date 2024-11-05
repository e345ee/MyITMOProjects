package products;

import java.io.Serializable;
import java.util.Objects;

public class Organization implements Serializable {


    private Long id; //Поле не может быть null, Значение поля должно быть больше 0, Значение этого поля должно быть уникальным, Значение этого поля должно генерироваться автоматически
    private String name; //Поле не может быть null, Строка не может быть пустой
    private long employeesCount; //Поле не может быть null, Значение поля должно быть больше 0
    private OrganizationType type; //Поле может быть null
    private Address officialAddress; //Поле может быть null

    public Organization(Long id, String name, long employeesCount, OrganizationType type, Address officialAddress) {
        this.id = id;
        this.name = name;
        this.employeesCount = employeesCount;
        this.type = type;
        this.officialAddress = officialAddress;
    }


    public Organization() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getEmployeesCount() {
        return employeesCount;
    }

    public void setEmployeesCount(Long employeesCount) {
        this.employeesCount = employeesCount;
    }

    public OrganizationType getType() {
        return type;
    }

    public void setType(OrganizationType type) {
        this.type = type;
    }

    public Address getOfficialAddress() {
        return officialAddress;
    }

    public void setOfficialAddress(Address officialAddress) {
        this.officialAddress = officialAddress;
    }

    @Override
    public String toString() {
        return "Organization{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", employeesCount=" + employeesCount +
                ", type=" + type +
                ", officialAddress=" + officialAddress +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Organization that)) return false;
        return getEmployeesCount() == that.getEmployeesCount() && Objects.equals(getName(), that.getName()) && getType() == that.getType() && Objects.equals(getOfficialAddress(), that.getOfficialAddress());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName(), getEmployeesCount(), getType(), getOfficialAddress());
    }
}
