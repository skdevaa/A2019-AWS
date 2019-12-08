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
import static com.automationanywhere.commandsdk.model.AttributeType.SELECT;
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
			                    @Idx(index = "2", type = AttributeType.SELECT ,  options = {
			                    		@Idx.Option(index = "2.1",pkg = @Pkg(label = "All",value = "ALL", default_value_type = STRING)),	
			                    		@Idx.Option(index = "2.2",pkg = @Pkg(label = "Word",value = "WORD", default_value_type = STRING)),
			                    		@Idx.Option(index = "2.3",pkg = @Pkg(label = "Line",value = "LINE", default_value_type = STRING))})
	                                 @Pkg(label = "Filter", default_value_type = STRING , default_value = "WORD") @NotEmpty String filter,
	 								@Idx(index = "3", type = SELECT, options = {
											@Idx.Option(index = "3.1", pkg = @Pkg(label = "US East (Ohio)", value = "us-east-2")),
											@Idx.Option(index = "3.2", pkg = @Pkg(label = "US East (N. Virginia)", value = "us-east-1")),
											@Idx.Option(index = "3.3", pkg = @Pkg(label = "US West (N. California", value = "us-west-1")),
											@Idx.Option(index = "3.4", pkg = @Pkg(label = "US West (Oregon)", value = "us-west-2")),
											@Idx.Option(index = "3.5", pkg = @Pkg(label = "Asia Pacific (Hong Kong)", value = "ap-east-1")),
											@Idx.Option(index = "3.6", pkg = @Pkg(label = "Asia Pacific (Mumbai)", value = "ap-south-1")),
											@Idx.Option(index = "3.7", pkg = @Pkg(label = "Asia Pacific (Osaka-Local)", value = "ap-northeast-3")),
											@Idx.Option(index = "3.8", pkg = @Pkg(label = "Asia Pacific (Seoul)", value = "ap-northeast-2")),
											@Idx.Option(index = "3.9", pkg = @Pkg(label = "Asia Pacific (Singapore)", value = "ap-southeast-1")),
											@Idx.Option(index = "3.10", pkg = @Pkg(label = "Asia Pacific (Sydney)", value = "ap-southeast-2")),
											@Idx.Option(index = "3.11", pkg = @Pkg(label = "Asia Pacific (Tokyo)", value = "ap-northeast-1")),
											@Idx.Option(index = "3.12", pkg = @Pkg(label = "Canada (Central)", value = "ca-central-1")),
											@Idx.Option(index = "3.13", pkg = @Pkg(label = "China (Beijing)", value = "cn-north-1")),
											@Idx.Option(index = "3.14", pkg = @Pkg(label = "China (Ningxia)", value = "cn-northwest-1")),
											@Idx.Option(index = "3.15", pkg = @Pkg(label = "EU (Frankfurt)", value = "eu-central-1")),
											@Idx.Option(index = "3.16", pkg = @Pkg(label = "EU (Ireland)", value = "eu-west-1")),
											@Idx.Option(index = "3.17", pkg = @Pkg(label = "EU (London)", value = "eu-west-2")),
											@Idx.Option(index = "3.18", pkg = @Pkg(label = "EU (Paris)", value = "eu-west-3")),
											@Idx.Option(index = "3.19", pkg = @Pkg(label = "EU (Stockholm)", value = "eu-north-1")),
											@Idx.Option(index = "3.20", pkg = @Pkg(label = "Middle East (Bahrain)", value = "me-south-1")),
											@Idx.Option(index = "3.21", pkg = @Pkg(label = "South America (Sao Paulo)", value = "sa-east-1")),
											@Idx.Option(index = "3.22", pkg = @Pkg(label = "AWS GovCloud (US-East)", value = "us-gov-east-1")),
											@Idx.Option(index = "3.23", pkg = @Pkg(label = "AWS GovCloud (US-West)", value = "us-gov-west-1"))
											}) @Pkg(label = "Region", default_value = "us-west-1", default_value_type = STRING) @NotEmpty String region,
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
