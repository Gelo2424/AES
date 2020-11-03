package module;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class AesViewModel {

    //textfield key
    private StringProperty keyProperty = new SimpleStringProperty();

    public StringProperty getKeyProperty() {
        return keyProperty;
    }

}
