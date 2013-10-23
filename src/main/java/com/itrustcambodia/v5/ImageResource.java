package com.itrustcambodia.v5;

import java.io.File;

import org.apache.wicket.request.resource.IResource;
import org.apache.wicket.request.resource.ResourceStreamResource;
import org.apache.wicket.util.resource.FileResourceStream;

public class ImageResource implements IResource {

    /**
     * 
     */
    private static final long serialVersionUID = 8318831452157176815L;

    private String file;

    public ImageResource(String file) {
        this.file = file;
    }

    @Override
    public void respond(Attributes attributes) {
        FileResourceStream fileResourceStream = new FileResourceStream(new File(file));
        ResourceStreamResource resource = new ResourceStreamResource(fileResourceStream);
        resource.respond(attributes);
    }

}
