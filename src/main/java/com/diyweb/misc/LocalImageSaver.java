package com.diyweb.misc;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Properties;
import java.util.UUID;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Named;
import jakarta.servlet.http.Part;

/**
 * Implementation of {@link ImageSaver} interface<br/>
 * This particular one can be used in order to save pictures locally<br/>
 * For now js http-server is used to provide host for images
 * @author erick
 *
 */
@Named("imageSaverBean")
@ApplicationScoped
public class LocalImageSaver implements ImageSaver {
	private static String propertyPath = Thread.currentThread().getContextClassLoader().getResource("images/images.properties").getPath();
	@Override
	public List<String> saveToLocation(String userEmail, LocalDateTime postedAt, int hashCode, Collection<Part> parts) throws FileNotFoundException, IOException {
		List<String> pictureUrls = new ArrayList<>();
		
		Properties imageProps = new Properties();
		
		imageProps.load(new FileInputStream(propertyPath));
		
		String picturesPersistFolder = "/posts/"+userEmail+"/"+postedAt+"/"+hashCode;
		String pathToPictures = imageProps.getProperty("images.save.path")+picturesPersistFolder;
		
		if(!Files.exists(Paths.get(pathToPictures))){
			System.out.println("Directory "+pathToPictures+" wasn't found, creating");
			Files.createDirectories(Paths.get(pathToPictures));
		}else if(Files.list(Paths.get(pathToPictures)).findFirst().isPresent()) {
			cleanUpDirectory(new File(pathToPictures));
			Files.createDirectories(Paths.get(pathToPictures));
		}
		
		for(Part part: parts) {
			if(part !=null && part.getSubmittedFileName()!=null && 
					(part.getSubmittedFileName().matches(".+\\.png$")
					|| part.getSubmittedFileName().matches(".+\\.gif$")
					|| part.getSubmittedFileName().matches(".+\\.jpg$")
					|| part.getSubmittedFileName().matches(".+\\.jpeg$"))) {
				
				
				Path filePath = Paths.get(pathToPictures+"/"+part.getSubmittedFileName());
				
				if(!Files.exists(filePath)) {
					System.out.println("File: "+filePath.toString()+" wasn't found, proceeding to create");
					Files.createFile(filePath);
				}
				
				InputStream pis = part.getInputStream();
				OutputStream pos = new FileOutputStream(filePath.toFile());
				pos.write(pis.readAllBytes());// for now all of the
				pictureUrls.add(filePath.toString().substring(filePath.toString().indexOf(picturesPersistFolder)));
			}
		}
		
		return pictureUrls;
	}
	
	/**
	 * Use this method to clean up folder after post was changed or deleted<br/>
	 * it removes post folder and it contents but postedAt will still be present<br/>
	 * if it contains any other post folder
	 * @param dir
	 * @throws IOException
	 */
	public static void cleanUpDirectory(File dir) throws IOException{
		if(!dir.exists()) return;
		//check if directory already exists and is empty, delete if true
		if(Files.list(dir.toPath()).findFirst().isEmpty()) dir.delete();
		
		File[] contents = dir.listFiles();
		//recursively go through files deleting files and emptying the folders before deletion 
		for(File element: contents) {
			if(element.isDirectory()) {
				cleanUpDirectory(element);
			}else {
				element.delete();
			}
		}
		//look up parent dir and delete it if it's empty
		File postedAtFolder = dir.getParentFile();
		dir.delete();
		if(Files.list(postedAtFolder.toPath()).findFirst().isEmpty()) postedAtFolder.delete();
	}

}
