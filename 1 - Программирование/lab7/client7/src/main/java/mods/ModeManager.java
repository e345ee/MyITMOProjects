package mods;

import exceptions.BuildObjectException;

public interface ModeManager<T> {

    T buildObject() throws BuildObjectException;
}