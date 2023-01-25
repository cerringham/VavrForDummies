package it.bitrock.model;

import io.vavr.Function3;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BasicModel {

    private String stringParameter;
    private Integer integerParameter;
    private List<AnotherBasicModel> listParameter;

    public String methodWhichAccepts3Parameters(String s1, String s2, String s3) {
        return new StringBuilder().append(s1).append(s2).append(s3).toString();
    }

    @Override
    public String toString() {
        String result =  stringParameter + " " + integerParameter + " ";

        if(listParameter != null)
            result += listParameter.size();

        return result;
    }
}
