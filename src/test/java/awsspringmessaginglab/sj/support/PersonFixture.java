package awsspringmessaginglab.sj.support;

public class PersonFixture {

    public static Person person(
        final String name,
        final int age
    ) {
        return new Person(name, age);
    }

    public record Person(
        String name,
        int age
    ) {}
}

