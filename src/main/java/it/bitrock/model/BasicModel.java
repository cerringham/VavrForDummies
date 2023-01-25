package it.bitrock.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BasicModel {

    private String stringParameter;
    private Integer integerParameter;
    private List<AnotherBasicModel> listParameter;

    @Override
    public String toString() {
        return stringParameter + " " + integerParameter + " " + listParameter.size();
    }
}
