import 'dart:async';
import 'dart:io' show Platform;

import 'package:flutter/services.dart';

class Conekta {
  static const MethodChannel _channel = const MethodChannel('conekta');

  static Future<String> get platformVersion async {
    final String version = await _channel.invokeMethod('getPlatformVersion');
    return version;
  }

  static set publicKey (String key) {
    _channel.invokeMethod('setPublicKey', {key: key});
  }

  static Future<String> tokenizeCard (Map info) async{
    final String token = await _channel.invokeMethod("tokenizeCard", info);
    return token;
  }
}
