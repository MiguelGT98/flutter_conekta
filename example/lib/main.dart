import 'package:flutter/material.dart';
import 'dart:async';

import 'package:flutter/services.dart';
import 'package:conekta/conekta.dart';

void main() => runApp(MyApp());

class MyApp extends StatefulWidget {
  @override
  _MyAppState createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  final _formKey = GlobalKey<FormState>();

  String _platformVersion = 'Unknown';
  String _name = "";
  String _number = "";
  String _expMonth = "";
  String _expYear = "";
  String _cvc = "";
  String _token = "";

  @override
  void initState() {
    super.initState();
    initPlatformState();
  }

  // Platform messages are asynchronous, so we initialize in an async method.
  Future<void> initPlatformState() async {
    String platformVersion;
    // Platform messages may fail, so we use a try/catch PlatformException.
    try {
      platformVersion = await Conekta.platformVersion;
    } on PlatformException {
      platformVersion = 'Failed to get platform version.';
    }

    // If the widget was removed from the tree while the asynchronous platform
    // message was in flight, we want to discard the reply rather than calling
    // setState to update our non-existent appearance.
    if (!mounted) return;

    setState(() {
      _platformVersion = platformVersion;
    });
  }

  Future<void> submit() async {
    if (_formKey.currentState.validate()) {
      _formKey.currentState.save();

      var info = Map();
      info["publicKey"] = "your_public_key";
      info["name"] = _name;
      info["number"] = _number;
      info["expMonth"] = _expMonth;
      info["expYear"] = _expYear;
      info["cvc"] = _cvc;

      String token;

      try {
        token = await Conekta.tokenizeCard(info);
      } catch (e) {
        print(e.toString());
        token = "Unable to tokenize card";
      }

      setState(() {
        _token = token;
      });
    }
  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
        theme: ThemeData(),
        title: "Conekta Tokenization Example",
        home: Scaffold(
            appBar: AppBar(
              title: Text("Conekta Tokenization Example"),
            ),
            body: Container(
                padding: EdgeInsets.all(20),
                child: new Form(
                    key: _formKey,
                    child: new ListView(
                      children: <Widget>[
                        SizedBox(height: 20),
                        cardNameField(),
                        SizedBox(height: 20),
                        cardNumberField(),
                        cardValidationRow(),
                        tokenizeCardButton(),
                        SizedBox(height: 20),
                        Text("Token: $_token")
                      ],
                    )))));
  }

  Widget cardNameField() {
    return Container(
        child: TextFormField(
      autofocus: true,
      keyboardType: TextInputType.text,
      enabled: true,
      decoration: InputDecoration(
          labelText: "Nombre del titular", border: OutlineInputBorder()),
      onSaved: (String value) {
        _name = value;
      },
      validator: validateCardNameField,
    ));
  }

  Widget cardNumberField() {
    return Container(
        child: TextFormField(
      keyboardType: TextInputType.number,
      maxLength: 16,
      enabled: true,
      decoration: InputDecoration(
          labelText: "Número de tarjeta", border: OutlineInputBorder()),
      onSaved: (String value) {
        _number = value;
      },
      validator: validateCardNumberField,
    ));
  }

  Widget cardExpirationMonthField() {
    return Expanded(
        flex: 2,
        child: TextFormField(
          maxLength: 2,
          keyboardType: TextInputType.number,
          enabled: true,
          decoration:
              InputDecoration(labelText: "MM", border: OutlineInputBorder()),
          onSaved: (String value) {
            _expMonth = value;
          },
          validator: validateExpMonthField,
        ));
  }

  Widget cardExpirationYearField() {
    return Expanded(
        flex: 4,
        child: TextFormField(
          maxLength: 4,
          keyboardType: TextInputType.number,
          enabled: true,
          decoration:
              InputDecoration(labelText: "YYYY", border: OutlineInputBorder()),
          onSaved: (String value) {
            _expYear = value;
          },
          validator: validateExpYearField,
        ));
  }

  Widget cardBackNumberField() {
    return Expanded(
        flex: 3,
        child: TextFormField(
          maxLength: 3,
          keyboardType: TextInputType.number,
          enabled: true,
          decoration:
              InputDecoration(labelText: "CVC", border: OutlineInputBorder()),
          onSaved: (String value) {
            _cvc = value;
          },
          validator: validateBackNumberField,
        ));
  }

  Widget cardValidationRow() {
    return Row(
      children: <Widget>[
        cardExpirationMonthField(),
        SizedBox(width: 20),
        cardExpirationYearField(),
        SizedBox(width: 20),
        cardBackNumberField(),
      ],
    );
  }

  Widget tokenizeCardButton() {
    return new Container(
      width: 240,
      height: 48,
      child: new RaisedButton(
        child: new Text(
          'Registrar tarjeta',
          style: new TextStyle(color: Colors.white),
        ),
        onPressed: () => this.submit(),
        color: Colors.blue,
      ),
      margin: new EdgeInsets.only(top: 20.0),
    );
  }

  String validateCardNameField(String value) {
    if (value.length == 0) {
      return "Por favor escribe el nombre";
    }
    if (!(value.contains(new RegExp("^[^0-9]+\$")))) {
      return "El nombre no puede incluír números";
    }
  }

  String validateCardNumberField(String value) {
    if (value.length != 16) {
      return "Se necesitan los 16 números";
    }
  }

  String validateExpMonthField(String value) {
    if (value.length != 2) {
      return "Escribe el més en el formato MM";
    }

    if (int.tryParse(value) < 1 || int.tryParse(value) > 12) {
      return "$value no es un mes válido";
    }

    //Validar de acuerdo a la fecha que resulta de mes y año
  }

  String validateExpYearField(String value) {
    if (value.length != 4) {
      return "Escribe el año en el formato YYYY";
    }

    //Validar de acuerdo a la fecha que resulta de mes y año
  }

  String validateBackNumberField(String value) {
    if (value.length != 3) {
      return "Se necesitan los tres números detrás de tu tarjeta";
    }
  }
}
