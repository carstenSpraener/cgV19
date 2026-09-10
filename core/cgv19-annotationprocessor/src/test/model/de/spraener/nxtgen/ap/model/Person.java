package de.spraener.nxtgen.ap.model;

import de.spraener.nxtgen.ap.meta.Entity;
import java.util.List;

@Entity(tableName="person")
class Person extends BasePerson implements Named {
    String name;
    int age;
    Address address;
    List<Address> addresses;

    public String getName() {
        return name;
    }

    public void setAge(int age) {
        this.age = age;
    }

    static class Nested {
        String x;
    }
}
