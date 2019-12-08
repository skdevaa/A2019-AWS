/*
 * Copyright (c) 2019 Automation Anywhere.
 * All rights reserved.
 *
 * This software is the proprietary information of Automation Anywhere.
 * You shall use it only in accordance with the terms of the license agreement
 * you entered into with Automation Anywhere.
 */
/**
 * 
 */
package com.automationanywhere.botcommand.sk;

import static com.automationanywhere.commandsdk.model.AttributeType.TEXT;
import static com.automationanywhere.commandsdk.model.AttributeType.CREDENTIAL;
import static com.automationanywhere.commandsdk.model.DataType.STRING;


import java.io.File;
import java.io.FileInputStream;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

import java.util.ArrayList;

import java.util.List;

import static com.automationanywhere.commandsdk.model.DataType.RECORD;


import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.rekognition.AmazonRekognition;
import com.amazonaws.services.rekognition.AmazonRekognitionClientBuilder;
import com.amazonaws.services.rekognition.model.DetectTextRequest;
import com.amazonaws.services.rekognition.model.DetectTextResult;
import com.amazonaws.services.rekognition.model.Image;
import com.amazonaws.services.rekognition.model.TextDetection;
import com.amazonaws.util.IOUtils;
import com.automationanywhere.botcommand.data.Value;
import com.automationanywhere.botcommand.data.impl.RecordValue;
import com.automationanywhere.botcommand.data.impl.StringValue;
import com.automationanywhere.botcommand.data.model.Schema;
import com.automationanywhere.botcommand.data.model.record.Record;
import com.automationanywhere.commandsdk.annotations.BotCommand;
import com.automationanywhere.commandsdk.annotations.CommandPkg;
import com.automationanywhere.commandsdk.annotations.Execute;
import com.automationanywhere.commandsdk.annotations.Idx;
import com.automationanywhere.commandsdk.annotations.Pkg;
import com.automationanywhere.commandsdk.annotations.rules.LocalFile;
import com.automationanywhere.commandsdk.annotations.rules.NotEmpty;
import com.automationanywhere.commandsdk.i18n.Messages;
import com.automationanywhere.commandsdk.i18n.MessagesFactory;
import com.automationanywhere.commandsdk.model.AttributeType;
import com.automationanywhere.core.security.SecureString;


/**
 * @author Stefan Karsten
 *
 */
@BotCommand
@CommandPkg(label="Text Recognition", name="detecttext", description="Detecting Text in a image ", icon="",
node_label="Detect text in image {{sourceFilePath}} ", return_type=RECORD, return_sub_type = STRING, return_label="Assign the output to variable", return_required=true)
public class Rekognition {
	
	
	@Execute
	public  Value<Record>  action(@Idx(index = "1", type = AttributeType.FILE) @LocalFile @Pkg(label = "File path" , default_value_type = STRING) @NotEmpty String sourceFilePath,
			                    @Idx(index = "2", type = AttributeType.SELECT /*,  options = {
			                    		@Idx.Option(index = "3",pkg = @Pkg(label = "All",value = "ALL", default_value_type = STRING)),	
			                    		@Idx.Option(index = "4",pkg = @Pkg(label = "Word",value = "WORD", default_value_type = STRING)),
			                    		@Idx.Option(index = "5",pkg = @Pkg(label = "Line",value = "LINE", default_value_type = STRING))}*/)
	                                 @Pkg(label = "Filter", default_value_type = STRING , default_value = "WORD")  String filter,

								@Idx(index = "6", type = TEXT) @Pkg(label = "AWS Region", default_value_type = STRING) @NotEmpty String region,
			                    @Idx(index = "7", type = CREDENTIAL) @Pkg(label = "Access Key", default_value_type = STRING) @NotEmpty SecureString access_key_id,
			                    @Idx(index = "8", type = CREDENTIAL)   @Pkg(label = "Secret Key", default_value_type = STRING ) @NotEmpty SecureString secret_key_id) throws IOException {
		
    		Record record;
    		Value<Record> valuerecord = new RecordValue();
    		List<Schema> schemas = new ArrayList<Schema>();
    		List<Value> values = new ArrayList<Value>();
    		
    		
    		String accesskey = access_key_id.getInsecureString();
    		String secretkey = secret_key_id.getInsecureString();

		    BasicAWSCredentials awsCreds = new BasicAWSCredentials(accesskey,secretkey);
	 
		    AmazonRekognition  rekognitionClient = AmazonRekognitionClientBuilder.standard()
	                                         .withCredentials(new AWSStaticCredentialsProvider(awsCreds))
	                                         .withRegion(region)
	                                         .build();
	        
	        
	        ByteBuffer imageBytes=null;
	        try (InputStream inputStream = new FileInputStream(new File(sourceFilePath))) {
	           imageBytes = ByteBuffer.wrap(IOUtils.toByteArray(inputStream));
	        }

	        Image image = new Image().withBytes(imageBytes);
			DetectTextRequest textrequest = new DetectTextRequest().withImage(image);
			DetectTextResult result = rekognitionClient.detectText(textrequest);
	        List<TextDetection> textDetections = result.getTextDetections();

	        filter = (filter == null) ? "WORD" : filter;
	         for (TextDetection text: textDetections) {
	    		String type = text.getType().toString();
	        	if (type.equals(filter) || filter.equals("ALL")) 
	        	{
	        		String detected = text.getDetectedText();
	    		    Schema schema =  new Schema();
	    		    schema.setName(type); 
	    		    schemas.add(schema);
	    		    values.add(new StringValue(detected));
	        	}
	         }
                 

	    	record = new Record();
	    	record.setSchema(schemas);
	    	record.setValues(values);

	    	valuerecord.set(record);
			return valuerecord;
	}
	

}
