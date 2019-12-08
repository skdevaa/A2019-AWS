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

import java.util.ArrayList;

import java.util.List;

import static com.automationanywhere.commandsdk.model.DataType.RECORD;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.comprehend.AmazonComprehend;
import com.amazonaws.services.comprehend.AmazonComprehendClientBuilder;
import com.amazonaws.services.comprehend.model.DetectEntitiesRequest;
import com.amazonaws.services.comprehend.model.Entity;
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
import com.automationanywhere.commandsdk.annotations.rules.NotEmpty;
import com.automationanywhere.core.security.SecureString;

/**
 * @author Stefan Karsten
 *
 */

@BotCommand
@CommandPkg(label="Named Entities", name="detectentity", description="Detecting Named Entities ", icon="",
node_label="Detect entities in {{sourceString}} using language {{language}}", return_type=RECORD, return_sub_type = STRING, return_label="Assign the output to variable", return_required=true)
public class DetetctEntities {

	
	@Execute
	public  Value<Record>  action(@Idx(index = "1", type = TEXT) @Pkg(label = "Source string", default_value_type = STRING) @NotEmpty String sourceString,
								@Idx(index = "2", type = TEXT) @Pkg(label = "Language", default_value_type = STRING) @NotEmpty String language,
								@Idx(index = "3", type = TEXT) @Pkg(label = "AWS Region", default_value_type = STRING) @NotEmpty String region,
			                    @Idx(index = "4", type = CREDENTIAL) @Pkg(label = "Access Key") @NotEmpty SecureString access_key_id,
			                    @Idx(index = "5", type = CREDENTIAL) @Pkg(label = "Secret Key") @NotEmpty SecureString secret_key_id) {
		

    		Value<Record> valuerecord = new RecordValue();
    		List<Schema> schemas = new ArrayList<Schema>();
    		List<Value> values = new ArrayList<Value>();
    		
    		
    		String accesskey = access_key_id.getInsecureString();
    		String secretkey = secret_key_id.getInsecureString();
    	

		    BasicAWSCredentials awsCreds = new BasicAWSCredentials(accesskey,secretkey);
	 
	        AmazonComprehend comprehendClient =
	            AmazonComprehendClientBuilder.standard()
	                                         .withCredentials(new AWSStaticCredentialsProvider(awsCreds))
	                                         .withRegion(region)
	                                         .build();
	                                         
	        // Call detectEntities API
	        DetectEntitiesRequest detectEntitiesRequest = new DetectEntitiesRequest().withText(sourceString)
	                                                                                 .withLanguageCode(language);
	        com.amazonaws.services.comprehend.model.DetectEntitiesResult detectEntitiesResult  = comprehendClient.detectEntities(detectEntitiesRequest);
	        List<Entity> entities = detectEntitiesResult.getEntities();
	       
	        Record record  = new Record();
	    	for (int i = 0; i < entities.size() ; i++) {
	    		 String type = entities.get(i).getType().toString();
	    		 String text = entities.get(i).getText().toString();

	    		 Schema schema =  new Schema();
	    		 schema.setName(type);	 
	    		 schemas.add(schema);
	    		 values.add(new StringValue(text));
			}


	    	valuerecord.set(record);
			return valuerecord;
	}
	

}
