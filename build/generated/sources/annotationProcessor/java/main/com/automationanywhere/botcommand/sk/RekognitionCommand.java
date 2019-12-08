package com.automationanywhere.botcommand.sk;

import com.automationanywhere.bot.service.GlobalSessionContext;
import com.automationanywhere.botcommand.BotCommand;
import com.automationanywhere.botcommand.data.Value;
import com.automationanywhere.botcommand.exception.BotCommandException;
import com.automationanywhere.commandsdk.i18n.Messages;
import com.automationanywhere.commandsdk.i18n.MessagesFactory;
import com.automationanywhere.core.security.SecureString;
import java.lang.ClassCastException;
import java.lang.Deprecated;
import java.lang.Object;
import java.lang.String;
import java.lang.Throwable;
import java.util.Map;
import java.util.Optional;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class RekognitionCommand implements BotCommand {
  private static final Logger logger = LogManager.getLogger(RekognitionCommand.class);

  private static final Messages MESSAGES_GENERIC = MessagesFactory.getMessages("com.automationanywhere.commandsdk.generic.messages");

  @Deprecated
  public Optional<Value> execute(Map<String, Value> parameters, Map<String, Object> sessionMap) {
    return execute(null, parameters, sessionMap);
  }

  public Optional<Value> execute(GlobalSessionContext globalSessionContext,
      Map<String, Value> parameters, Map<String, Object> sessionMap) {
    logger.traceEntry(() -> parameters != null ? parameters.toString() : null, ()-> sessionMap != null ?sessionMap.toString() : null);
    Rekognition command = new Rekognition();
    if(parameters.get("sourceFilePath") == null || parameters.get("sourceFilePath").get() == null) {
      throw new BotCommandException(MESSAGES_GENERIC.getString("generic.validation.notEmpty","sourceFilePath"));
    }

    if(parameters.get("filter") != null && parameters.get("filter").get() != null && !(parameters.get("filter").get() instanceof String)) {
      throw new BotCommandException(MESSAGES_GENERIC.getString("generic.UnexpectedTypeReceived","filter", "String", parameters.get("filter").get().getClass().getSimpleName()));
    }

    if(parameters.get("region") == null || parameters.get("region").get() == null) {
      throw new BotCommandException(MESSAGES_GENERIC.getString("generic.validation.notEmpty","region"));
    }

    if(parameters.get("access_key_id") == null || parameters.get("access_key_id").get() == null) {
      throw new BotCommandException(MESSAGES_GENERIC.getString("generic.validation.notEmpty","access_key_id"));
    }

    if(parameters.get("secret_key_id") == null || parameters.get("secret_key_id").get() == null) {
      throw new BotCommandException(MESSAGES_GENERIC.getString("generic.validation.notEmpty","secret_key_id"));
    }

    if(parameters.get("sourceFilePath") != null && parameters.get("sourceFilePath").get() != null && !(parameters.get("sourceFilePath").get() instanceof String)) {
      throw new BotCommandException(MESSAGES_GENERIC.getString("generic.UnexpectedTypeReceived","sourceFilePath", "String", parameters.get("sourceFilePath").get().getClass().getSimpleName()));
    }
    if(parameters.get("filter") != null && parameters.get("filter").get() != null && !(parameters.get("filter").get() instanceof String)) {
      throw new BotCommandException(MESSAGES_GENERIC.getString("generic.UnexpectedTypeReceived","filter", "String", parameters.get("filter").get().getClass().getSimpleName()));
    }
    if(parameters.get("region") != null && parameters.get("region").get() != null && !(parameters.get("region").get() instanceof String)) {
      throw new BotCommandException(MESSAGES_GENERIC.getString("generic.UnexpectedTypeReceived","region", "String", parameters.get("region").get().getClass().getSimpleName()));
    }
    if(parameters.get("access_key_id") != null && parameters.get("access_key_id").get() != null && !(parameters.get("access_key_id").get() instanceof SecureString)) {
      throw new BotCommandException(MESSAGES_GENERIC.getString("generic.UnexpectedTypeReceived","access_key_id", "SecureString", parameters.get("access_key_id").get().getClass().getSimpleName()));
    }
    if(parameters.get("secret_key_id") != null && parameters.get("secret_key_id").get() != null && !(parameters.get("secret_key_id").get() instanceof SecureString)) {
      throw new BotCommandException(MESSAGES_GENERIC.getString("generic.UnexpectedTypeReceived","secret_key_id", "SecureString", parameters.get("secret_key_id").get().getClass().getSimpleName()));
    }
    try {
      Optional<Value> result =  Optional.ofNullable(command.action(parameters.get("sourceFilePath") != null ? (String)parameters.get("sourceFilePath").get() : (String)null ,parameters.get("filter") != null ? (String)parameters.get("filter").get() : (String)null ,parameters.get("region") != null ? (String)parameters.get("region").get() : (String)null ,parameters.get("access_key_id") != null ? (SecureString)parameters.get("access_key_id").get() : (SecureString)null ,parameters.get("secret_key_id") != null ? (SecureString)parameters.get("secret_key_id").get() : (SecureString)null ));
      logger.traceExit(result);
      return result;
    }
    catch (ClassCastException e) {
      throw new BotCommandException(MESSAGES_GENERIC.getString("generic.IllegalParameters","action"));
    }
    catch (BotCommandException e) {
      logger.fatal(e.getMessage(),e);
      throw e;
    }
    catch (Throwable e) {
      logger.fatal(e.getMessage(),e);
      throw new BotCommandException(MESSAGES_GENERIC.getString("generic.NotBotCommandException",e.getMessage()),e);
    }
  }
}
