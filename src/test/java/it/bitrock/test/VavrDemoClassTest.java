package it.bitrock.test;

import io.vavr.*;
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

class VavrDemoClassTest {

    // ### TRY ###
    @Test
    void divideVavrTest() {
        VavrDemoClass vavrDemoClass = new VavrDemoClass();
        Try<Integer> result = vavrDemoClass.divideVavr(12, 0);

        assertEquals(Boolean.TRUE, result.isFailure());
    }

    // ### OPTION ###
    @Test
    void optionTest() {
        Option<Object> noneOption = Option.of(null);
        Option<Object> someOption = Option.of("some option");

        assertEquals("None", noneOption.toString());
        assertEquals("Some(some option)", someOption.toString());
        assertEquals("some option", someOption.get());
    }

    @Test
    void getOrElseTestWithNullObject() {
        Option<BasicModel> basicModelOption = Option.of(null);
        BasicModel basicModel = basicModelOption.getOrElse(new BasicModel("Something in the way", 1, new ArrayList<>()));

        // sad but beautiful Nirvana song!!!
        assertEquals("Something in the way", basicModel.getStringParameter());
    }

    @Test
    void getOrElseTestWithNotNullObject() {
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
    void whenCreatesTupleThenCorrect3() {
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
    void givenFunctionWhenEvaluatesWithLazyThenCorrect() {
        Lazy<Double> lazy = Lazy.of(Math::random);
        assertFalse(lazy.isEvaluated());

        double val1 = lazy.get();
        assertTrue(lazy.isEvaluated());

        double val2 = lazy.get();
        assertEquals(val1, val2);
    }

    // ### PATTERN MATCHING ###
    @Test
    void whenMatchworksThenCorrect() {
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

    // ### VALIDATION
    @Test
    void testBasicModelValidationThenCorrect() {
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

    // ### FUNCTION
    // Java 8 just provides a Function which accepts one parameter and a BiFunction which accepts two parameters.
    // Vavr provides functions up to a limit of 8 parameters. The functional interfaces are of called Function0,
    // Function1, Function2, Function3 and so on.
    // If you need a function which throws a checked exception you can use CheckedFunction1, CheckedFunction2 and so on.

    @Test
    void function3TestThenCorrect() {
        BasicModel basicModel = new BasicModel("", 0, null);

        Function3<String, String, String, String> function3 =
                Function3.of(basicModel::methodWhichAccepts3Parameters);

        String result = function3.apply("come, ", "as you are, ", "as you were");
        basicModel.setStringParameter(result);
        assertEquals("come, as you are, as you were", basicModel.getStringParameter());
    }

    // Lifting
    // You can lift a partial function into a total function that returns an Option result.
    // The term partial function comes from mathematics.
    // A partial function from X to Y is a function f: X′ → Y, for some subset X′ of X.
    // It generalizes the concept of a function f: X → Y by not forcing f to map every element of X to an element of Y.
    // That means a partial function works properly only for some input values.
    // If the function is called with a disallowed input value, it will typically throw an exception.
    @Test
    void liftingTestThenCorrect() {
        Function2<Integer, Integer, Integer> divide = (a, b) -> a / b;

        Function2<Integer, Integer, Option<Integer>> safeDivide = Function2.lift(divide);

        Option<Integer> i1 = safeDivide.apply(1, 0);
        Option<Integer> i2 = safeDivide.apply(4, 2);

        assertEquals(None(), i1);
        assertEquals(Some(2), i2);
    }

    // Memoization
    // is a form of caching. A memoized function executes only once and then returns the result from a cache.
    @Test
    void memoizationTestThenCorrect() {
        Function0<Double> hashCache =
                Function0.of(Math::random).memoized();

        double randomValue1 = hashCache.apply();
        double randomValue2 = hashCache.apply();

        assertEquals(randomValue1, randomValue2);
    }

    // last but not least ...
    // ### COLLECTION
    // https://www.javadoc.io/doc/io.vavr/vavr/latest/index.html
    // https://www.javadoc.io/static/io.vavr/vavr/1.0.0-alpha-4/io/vavr/collection/Traversable.html
    // Collections in Vavr are immutable
    @Test
    void testCollectionThenCorrect() {
        io.vavr.collection.List<Integer> intList = io.vavr.collection.List.of(1, 2, 3);

        assertEquals(3, intList.length());
        assertEquals(new Integer(1), intList.get(0));
        assertEquals(new Integer(2), intList.get(1));
        assertEquals(new Integer(3), intList.get(2));

        assertEquals(6, intList.sum().intValue());
    }
}
