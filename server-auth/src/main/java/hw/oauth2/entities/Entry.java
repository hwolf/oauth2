package hw.oauth2.entities;

import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class Entry {

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String data;

    public static Entry create(String name, String value) {
        Entry entry = new Entry();
        entry.setName(name);
        entry.setData(value);
        return entry;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, data);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Entry other = (Entry) obj;
        return Objects.equals(name, other.name) && Objects.equals(data, other.data);
    }
}
