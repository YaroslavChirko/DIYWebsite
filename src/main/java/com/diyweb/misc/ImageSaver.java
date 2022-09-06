package com.diyweb.misc;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import jakarta.servlet.http.Part;

public interface ImageSaver {
	public List<String> saveToLocation(UUID userIdentifier, int hashCode, Collection<Part> parts) throws FileNotFoundException, IOException;
}
