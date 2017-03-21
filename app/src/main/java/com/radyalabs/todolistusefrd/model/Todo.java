package com.radyalabs.todolistusefrd.model;

/**
 * Created by aderifaldi on 20-Mar-17.
 */

public class Todo {

    public String id, item;
    public boolean is_done;

    public Todo(){
    }

    public Todo(String id, String item, boolean is_done) {
        this.id = id;
        this.item = item;
        this.is_done = is_done;
    }
}
