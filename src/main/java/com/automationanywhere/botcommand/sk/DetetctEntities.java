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
import static com.automationanywhere.commandsdk.model.AttributeType.SELECT;
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

	    	record = new Record();
	    	record.setSchema(schemas);
	    	record.setValues(values);
	    	valuerecord.set(record);
			return valuerecord;
	}
	

}
