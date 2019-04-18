package xyz.gallardo.conekta;

import android.app.Activity;

import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
import io.flutter.plugin.common.PluginRegistry.Registrar;

import io.conekta.conektasdk.Conekta;
import io.conekta.conektasdk.Card;
import io.conekta.conektasdk.Token;

import java.util.Map;

import org.json.JSONObject;
import android.util.Log;

/** ConektaPlugin */
public class ConektaPlugin implements MethodCallHandler {
  private final Activity activity;

  /** Plugin registration. */
  public static void registerWith(Registrar registrar) {
    final MethodChannel channel = new MethodChannel(registrar.messenger(), "conekta");
    channel.setMethodCallHandler(new ConektaPlugin(registrar.activity()));
  }

  private ConektaPlugin(Activity activity) {
    this.activity = activity;
  }

  public void tokenizeCard(final Result result, Activity activity, String publicKey, String name, String number, String cvc, String expMonth, String expYear) {
    Conekta.setPublicKey(publicKey);
    Conekta.collectDevice(activity);
    
    Card card = new Card(name, number, cvc, expMonth, expYear);
    Token token = new Token(activity);


    token.onCreateTokenListener(new Token.CreateToken() {
      @Override
      public void onCreateTokenReady(JSONObject data) {
        try {
          result.success(data.getString("id"));
        } catch (Exception err) {
          result.error("UNABLE TO TOKENIZE CARD", err.getMessage(), null);
        }
      }
    });

    token.create(card);
  }

  @Override
  public void onMethodCall(MethodCall call, Result result) {
    if (call.method.equals("getPlatformVersion")) {
      result.success("Android " + android.os.Build.VERSION.RELEASE);
      return;
    }

    if (call.method.equals("tokenizeCard")) {
      String publicKey = call.argument("publicKey");
      String name = call.argument("name");
      String number = call.argument("number");
      String cvc = call.argument("cvc");
      String expYear = call.argument("expYear");
      String expMonth = call.argument("expMonth");

      tokenizeCard(result, activity, publicKey, name, number, cvc, expMonth, expYear);
      return;
    }

    result.notImplemented();
    return;
  }
}
