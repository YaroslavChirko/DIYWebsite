package com.diyweb.misc;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import jakarta.servlet.http.Part;

public interface ImageSaver {
	public List<String> saveToLocation(String userEmail, LocalDateTime postedAt, int hashCode, Collection<Part> parts) throws FileNotFoundException, IOException;
}
