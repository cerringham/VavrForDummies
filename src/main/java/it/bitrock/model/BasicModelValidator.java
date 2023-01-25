package it.bitrock.model;

import io.vavr.collection.Seq;
import io.vavr.control.Validation;

import java.util.List;

public class BasicModelValidator {
    final static String STRING_ERR = "Invalid characters in stringParameter: ";
    final static String INTEGER_ERR = "Integer must be greater than 0";
    final static String LIST_ERR = "List must be not empty";

    public Validation<Seq<String>, BasicModel> validateBasicModel(
            BasicModel basicModel) {
        return Validation.combine(
                validateString(basicModel.getStringParameter()), validateInteger(basicModel.getIntegerParameter()),
                validateList(basicModel.getListParameter())).ap(BasicModel::new);
    }

    private Validation<String, String> validateString(String string) {
        String invalidChars = string.replaceAll("[a-zA-Z ]", "");
        return invalidChars.isEmpty() ?
                Validation.valid(string)
                : Validation.invalid(STRING_ERR + invalidChars);
    }

    private Validation<String, Integer> validateInteger(Integer integer) {
        return integer <= 0 ? Validation.invalid(INTEGER_ERR)
                : Validation.valid(integer);
    }

    private Validation<String, List<AnotherBasicModel>> validateList(List<AnotherBasicModel> list) {
        return list == null || list.isEmpty() ? Validation.invalid(LIST_ERR)
                : Validation.valid(list);
    }
}
