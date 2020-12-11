package com.redhat.fuse.boosters.cb;


public class PersonWrapper {

    public PersonWrapper(Person person){
        this.person = person;
    }

    public PersonWrapper(){}

    private Person person;

    public Person getPerson() {
		return person;
	}

	public void setPerson(Person person) {
		this.person = person;
	}

}