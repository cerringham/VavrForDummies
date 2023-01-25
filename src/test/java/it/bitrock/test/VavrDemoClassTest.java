package it.bitrock.test;

import io.vavr.Lazy;
import io.vavr.Tuple;
import io.vavr.Tuple3;
import io.vavr.collection.Seq;
import io.vavr.control.Option;
import io.vavr.control.Try;
import io.vavr.control.Validation;
import it.bitrock.function.VavrDemoClass;
import it.bitrock.model.AnotherBasicModel;
import it.bitrock.model.BasicModel;
import it.bitrock.model.BasicModelValidator;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static io.vavr.API.*;
import static io.vavr.Predicates.isIn;
import static org.junit.jupiter.api.Assertions.*;

public class VavrDemoClassTest {

    // ### TRY ###
    @Test
    public void divideVavrTest() {
        VavrDemoClass vavrDemoClass = new VavrDemoClass();
        Try<Integer> result = vavrDemoClass.divideVavr(12, 0);

        assertEquals(Boolean.TRUE, result.isFailure());
    }

    // ### OPTION ###
    @Test
    public void optionTest() {
        Option<Object> noneOption = Option.of(null);
        Option<Object> someOption = Option.of("some option");

        assertEquals("None", noneOption.toString());
        assertEquals("Some(some option)", someOption.toString());
        assertEquals("some option", someOption.get());
    }

    @Test
    public void getOrElseTestWithNullObject() {
        Option<BasicModel> basicModelOption = Option.of(null);
        BasicModel basicModel = basicModelOption.getOrElse(new BasicModel("Something in the way", 1, new ArrayList<>()));

        // sad but beautiful Nirvana song!!!
        assertEquals("Something in the way", basicModel.getStringParameter());
    }

    @Test
    public void getOrElseTestWithNotNullObject() {
        Option<BasicModel> basicModelOption = Option.of(new BasicModel("About a girl", 12, new ArrayList<>()));
        BasicModel basicModel = basicModelOption.getOrNull();

        assertNotNull(basicModel);
        // an happy Nirvana song!!!
        assertEquals("About a girl", basicModel.getStringParameter());
    }

    // ### TUPLE ###
    // Tuples are immutable and can hold multiple objects of different types in a type-safe manner
    // Tuple1, Tuple2 to Tuple8 epending on the number of elements they are to take
    private Tuple3 createTuple3() {
        AnotherBasicModel anotherBasicModel = new AnotherBasicModel(5.6f);
        BasicModel basicModel = new BasicModel("The Number of the Beast", 666, List.of(anotherBasicModel));
        Tuple3<BasicModel, Integer, AnotherBasicModel> aTuple = Tuple.of(basicModel, 777, anotherBasicModel);

        return aTuple;
    }

    @Test
    public void whenCreatesTupleThenCorrect3() {
        Tuple3<BasicModel, Integer, AnotherBasicModel> myTuple = createTuple3();
        Tuple3<BasicModel, Integer, AnotherBasicModel> updatedTuple = myTuple.update2(555);

        // if Tuple's elements types are not specified cast from Object is required
        BasicModel element1 = myTuple._1;
        int element2 = myTuple._2();
        AnotherBasicModel element3 = myTuple._3(); // getter

        assertNotNull(element1);
        assertEquals(777, element2);
        assertEquals(5.6f, element3.getFloatParameter());

        assertEquals(555, updatedTuple._2);
    }

    // ### LAZY ###
    // Lazy is a container which represents a value computed lazily i.e. computation is deferred until the result is
    // required. Furthermore, the evaluated value is cached or memoized and returned again and again each time it is
    // needed without repeating the computation
    @Test
    public void givenFunctionWhenEvaluatesWithLazyThenCorrect() {
        Lazy<Double> lazy = Lazy.of(Math::random);
        assertFalse(lazy.isEvaluated());

        double val1 = lazy.get();
        assertTrue(lazy.isEvaluated());

        double val2 = lazy.get();
        assertEquals(val1, val2);
    }

    // ### PATTERN MATCHING ###
    @Test
    public void whenMatchworksThenCorrect() {
        int input = 2;
        String output = Match(input).of(
                Case($(1), "one"),
                Case($(2), "two"),
                Case($(3), "three"),
                Case($(), "?")); // no match

        assertEquals("two", output);

        String arg = "h";
        Match(arg).of(
                Case($(isIn("-h", "--help")), o -> run(this::displayHelp)),
                Case($(isIn("-v", "--version")), o -> run(this::displayVersion)),
                Case($(), o -> run(() -> {
                    throw new IllegalArgumentException(arg);
                }))
        );
    }

    private void displayVersion() {
        System.out.println("1.x");
    }

    private void displayHelp() {
        System.out.println("Help me");
    }

    // ### VALIDATIOM
    @Test
    public void testBasicModelValidationThenCorrect() {
        Tuple3<BasicModel, Integer, AnotherBasicModel> tuple3 = createTuple3();

        BasicModel basicModel = tuple3._1;
        BasicModel invalidBasicModel = new BasicModel("invallid basic model", 1, null);
        BasicModel anotherInvalidBasicModel = new BasicModel("invallid basic model", 0, null);

        BasicModelValidator basicModelValidator = new BasicModelValidator();

        // Seq ... the show must go on
        Validation<Seq<String>, BasicModel> valid =
                basicModelValidator.validateBasicModel(basicModel);

        Validation<Seq<String>, BasicModel> invalid =
                basicModelValidator.validateBasicModel(invalidBasicModel);

        Validation<Seq<String>, BasicModel> anotherInvalid =
                basicModelValidator.validateBasicModel(anotherInvalidBasicModel);

        assertEquals(
                "Valid(The Number of the Beast 666 1)", // use toString of BasicModel
                valid.toString());

        assertEquals(
                "Invalid(List(List must be not empty))",
                invalid.toString());

        assertEquals(
                "Invalid(List(Integer must be greater than 0, List must be not empty))",
                anotherInvalid.toString());
    }

    // last but not least ...
    // ### COLLECTION
    // https://www.javadoc.io/doc/io.vavr/vavr/latest/index.html
    // https://www.javadoc.io/static/io.vavr/vavr/1.0.0-alpha-4/io/vavr/collection/Traversable.html
    // Collections in Vavr are immutable
    @Test
    public void testCollectionThenCorrect() {
        io.vavr.collection.List<Integer> intList = io.vavr.collection.List.of(1, 2, 3);

        assertEquals(3, intList.length());
        assertEquals(new Integer(1), intList.get(0));
        assertEquals(new Integer(2), intList.get(1));
        assertEquals(new Integer(3), intList.get(2));

        assertEquals(6, intList.sum().intValue());
    }
}
