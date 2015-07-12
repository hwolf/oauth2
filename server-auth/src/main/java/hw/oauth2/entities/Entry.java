package hw.oauth2.entities;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class Entry {

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String data;

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
}
