package com.example.flutter_conekta;

import android.app.Activity;

import org.json.JSONObject;

import java.util.Map;

import io.conekta.conektasdk.Card;
import io.conekta.conektasdk.Conekta;
import io.conekta.conektasdk.Token;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
import io.flutter.plugin.common.PluginRegistry.Registrar;

/** FlutterConektaPlugin */
public class FlutterConektaPlugin implements MethodCallHandler {

  private final Activity activity;

  /** Plugin registration. */
  public static void registerWith(Registrar registrar) {
    final MethodChannel channel = new MethodChannel(registrar.messenger(), "flutter_conekta");
    channel.setMethodCallHandler(new FlutterConektaPlugin(registrar.activity()));
  }

  private FlutterConektaPlugin(Activity activity) {
    this.activity = activity;
  }

  @Override
  public void onMethodCall(MethodCall call, Result result) {
    if (call.method.equals("tokenizeCard")) {
      handleTokenizeCard(call, result);
    } else {
      result.notImplemented();
    }
  }

  private void handleTokenizeCard(MethodCall call, final Result result) {
    Map<String, String> arguments = call.arguments();
    String publicKey = arguments.get("publicKey");
    String cardholderName = arguments.get("cardholderName");
    String cardNumber = arguments.get("cardNumber");
    String cvv = arguments.get("cvv");
    String expiryMonth = arguments.get("expiryMonth");
    String expiryYear = arguments.get("expiryYear");

    Conekta.setPublicKey(publicKey);
    Conekta.collectDevice(this.activity);

    Card card = new Card(cardholderName, cardNumber, cvv, expiryMonth, expiryYear);

    Token token = new Token(this.activity);

    token.onCreateTokenListener(new Token.CreateToken() {
      @Override
      public void onCreateTokenReady(JSONObject data) {
        try {
          result.success(data.getString("id"));
        } catch (Exception err) {
          result.error("ERROR_UNABLE_TO_TOKENIZE", err.getMessage(), null);
        }
      }
    });

    token.create(card);
  }
}
