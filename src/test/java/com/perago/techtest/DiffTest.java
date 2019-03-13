package com.perago.techtest;

import com.perago.techtest.test.Person;
import com.perago.techtest.test.Pet;
import org.junit.Before;
import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.*;

public class DiffTest {

    @Test
    public void diffShouldCreateAPersonIfOriginalIsNull() throws Exception {
        DiffEngine diffEngine = new Diff();

        Person modifiedPerson = new Person();

        modifiedPerson.setFirstName("Fred");
        modifiedPerson.setSurname("Jones");
        modifiedPerson.setFriend(getModifiedFriend());
        modifiedPerson.setPet(getModifiedPet());
        modifiedPerson.setNickNames(getModifiedNickNames());

        Diff<Person> diff = diffEngine.calculate(null, modifiedPerson);
        assertNotNull(diff);
        assertEquals(diff.getNewInstanceHolder(), modifiedPerson);
    }

    @Test
    public void diffShouldDeleteAPersonIfModifiedIsNull() throws Exception {
        DiffEngine diffEngine = new Diff();

        Person originalPerson = new Person();

        originalPerson.setFirstName("Fred");
        originalPerson.setSurname("Smith");
        originalPerson.setFriend(getOriginalFriend());
        originalPerson.setPet(getOriginalPet());
        originalPerson.setNickNames(getOriginalNickNames());

        Diff<Person> diff = diffEngine.calculate(originalPerson, null);
        assertNull(diff);
    }

    @Test
    public void diffShouldUpdateAPersonWhenBothOriginalAndModifiedAreNotNull() throws Exception {
        DiffEngine diffEngine = new Diff();

        Person originalPerson = new Person();

        originalPerson.setFirstName("Fred");
        originalPerson.setSurname("Smith");
        originalPerson.setFriend(getOriginalFriend());
        originalPerson.setPet(getOriginalPet());
        originalPerson.setNickNames(getOriginalNickNames());

        Person modifiedPerson = new Person();

        modifiedPerson.setFirstName("Fred");
        modifiedPerson.setSurname("Jones");
        modifiedPerson.setFriend(getModifiedFriend());
        modifiedPerson.setPet(getModifiedPet());
        modifiedPerson.setNickNames(getModifiedNickNames());

        Diff<Person> diff = diffEngine.calculate(originalPerson, modifiedPerson);
        assertNotNull(diff);
    }

    @Test
    public void diffShouldUpdateAndCreateCertainFieldsWhenBothOriginalAndModifiedAreNotNull() throws Exception {
        DiffEngine diffEngine = new Diff();

        Person originalPerson = new Person();

        originalPerson.setFirstName("Fred");
        originalPerson.setSurname("Smith");
        originalPerson.setFriend(getOriginalFriend());

        Person modifiedPerson = new Person();

        modifiedPerson.setFirstName("Fred");
        modifiedPerson.setSurname("Jones");
        modifiedPerson.setFriend(getModifiedFriend());
        modifiedPerson.setPet(getModifiedPet());
        modifiedPerson.setNickNames(getModifiedNickNames());

        Diff<Person> diff = diffEngine.calculate(originalPerson, modifiedPerson);
        assertNotNull(diff);
    }

    @Test
    public void diffShouldUpdateAndDeleteCertainFieldsWhenBothOriginalAndModifiedAreNotNull() throws Exception {
        DiffEngine diffEngine = new Diff();

        Person originalPerson = new Person();

        originalPerson.setFirstName("Fred");
        originalPerson.setSurname("Smith");
        originalPerson.setFriend(getOriginalFriend());
        originalPerson.setPet(getOriginalPet());
        originalPerson.setNickNames(getOriginalNickNames());

        Person modifiedPerson = new Person();

        modifiedPerson.setFirstName("Fred");
        modifiedPerson.setSurname("Jones");

        Diff<Person> diff = diffEngine.calculate(originalPerson, modifiedPerson);
        assertNotNull(diff);
    }

    private Person getOriginalFriend() {
        Person person = new Person();

        person.setFirstName("Tom");
        person.setSurname("Brown");

        return person;
    }

    private Pet getOriginalPet() {
        Pet pet = new Pet();

        pet.setType("Dog");
        pet.setName("Rover");

        return pet;
    }

    private Set<String> getOriginalNickNames() {
        Set<String> nickNames = new HashSet<String>();

        nickNames.add("scooter");
        nickNames.add("biff");

        return nickNames;
    }

    private Person getModifiedFriend() {
        Person person = new Person();

        person.setFirstName("Jim");
        person.setSurname("Brown");

        return person;
    }

    private Pet getModifiedPet() {
        Pet pet = new Pet();

        pet.setType("Dog");
        pet.setName("Spot");

        return pet;
    }

    private Set<String> getModifiedNickNames() {
        Set<String> nickNames = new HashSet<String>();

        nickNames.add("biff");
        nickNames.add("polly");

        return nickNames;
    }


}