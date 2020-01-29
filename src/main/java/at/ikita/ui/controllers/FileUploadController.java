package at.ikita.ui.controllers;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.primefaces.event.FileUploadEvent;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;


/**
 * 
 * A Controller to upload Files
 * 
 * 
 * @author daniel.walder@student.uibk.ac.at
 *
 */
@Component
@Scope("view")
public class FileUploadController implements Serializable {

	
	
	
	/**
	 * 
	 * Converts the input Stream into an output Stream
	 * 
	 * @param event File to upload
	 * @return a path where the file is stored
	 */
    public String handleFileUpload(FileUploadEvent event) {

        String filename = FilenameUtils.getName(event.getFile().getFileName());
        String basename = FilenameUtils.getBaseName(filename);
        String extension = "." + FilenameUtils.getExtension(filename);
        
  
        OutputStream output = null;
        InputStream input = null;
        File file = null;

        try {
        	String path = System.getProperty("user.dir") + "/src/main/webapp/resources/images";
        	file = File.createTempFile(basename, extension, new File(path));
			input = event.getFile().getInputstream();
		    output = new FileOutputStream(file);
			IOUtils.copy(input,  output);

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        finally{
        	IOUtils.closeQuietly(input);
        	IOUtils.closeQuietly(output);
        }
   
        String[] splits = file.getAbsolutePath().split("/src/main/webapp");
        String database_string = splits[1];
        
		return database_string;

   
	}
	
	
        
        
        
        
}

	
	
	
	
	
