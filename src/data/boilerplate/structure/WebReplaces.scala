package data.boilerplate.structure

class Replacement(val pattern: String, val replace: String) {
  def replaceAll(text: String): String = {
    text.replaceAll(pattern, replace)
  }

  def swapReplaceAll(text: String): String = {
    text.replaceAll(replace, pattern)
  }
}


object WebReplaces {
  val htmlReplaces = Array(
    new Replacement("&nbsp;", " "), new Replacement("&iexcl;", "¡"), new Replacement("&cent;", ""), new Replacement("&pound;", "£"),
    new Replacement("&curren;", "¤"), new Replacement("&yen;", "¥"), new Replacement("&brvbar;", "¦"), new Replacement("&sect;", "§"),
    new Replacement("&uml;", "¨"), new Replacement("&copy;", "©"), new Replacement("&ordf;", "ª"), new Replacement("&laquo;", "«"),
    new Replacement("&not;", "¬"), new Replacement("&shy;", ""), new Replacement("&reg;", "®"), new Replacement("&macr;", "¯"),
    new Replacement("&deg;", "°"), new Replacement("&plusmn;", "±"), new Replacement("&sup2;", "²"), new Replacement("&sup3;", "³"),
    new Replacement("&acute;", "´"), new Replacement("&micro;", "µ"), new Replacement("&para;", "¶"), new Replacement("&middot;", "·"),
    new Replacement("&cedil;", "¸"), new Replacement("&sup1;", "¹"), new Replacement("&ordm;", "º"), new Replacement("&raquo;", "»"),
    new Replacement("&frac14;", "¼"), new Replacement("&frac12;", "½"), new Replacement("&frac34;", "¾"), new Replacement("&iquest;", "¿"),
    new Replacement("&Agrave;", "À"), new Replacement("&Aacute;", "Á"), new Replacement("&Acirc;", "Â"), new Replacement("&Atilde;", "Ã"),
    new Replacement("&Auml;", "Ä"), new Replacement("&Aring;", "Å"), new Replacement("&AElig;", "Æ"), new Replacement("&Ccedil;", "Ç"),
    new Replacement("&Egrave;", "È"), new Replacement("&Eacute;", "É"), new Replacement("&Ecirc;", "Ê"), new Replacement("&Euml;", "Ë"),
    new Replacement("&Igrave;", "Ì"), new Replacement("&Iacute;", "Í"), new Replacement("&Icirc;", "Î"), new Replacement("&Iuml;", "Ï"),
    new Replacement("&ETH;", "Ð"), new Replacement("&Ntilde;", "Ñ"), new Replacement("&Ograve;", "Ò"), new Replacement("&Oacute\\;", "Ó"),
    new Replacement("&Ocirc;", "Ô"), new Replacement("&Otilde;", "Õ"), new Replacement("&Ouml;", "Ö"), new Replacement("&times;", "×"),
    new Replacement("&Oslash;", "Ø"), new Replacement("&Ugrave;", "Ù"), new Replacement("&Uacute;", "Ú"), new Replacement("&Ucirc;", "Û"),
    new Replacement("&Uuml;", "Ü"), new Replacement("&Yacute;", "Ý"), new Replacement("&THORN;", "Þ"), new Replacement("&szlig;", "ß"),
    new Replacement("&agrave;", "à"), new Replacement("&aacute;", "á"), new Replacement("&acirc;", "â"), new Replacement("&atilde;", "ã"),
    new Replacement("&auml;", "ä"), new Replacement("&aring;", "å"), new Replacement("&aelig;", "æ"), new Replacement("&ccedil;", "ç"),
    new Replacement("&egrave;", "è"), new Replacement("&eacute;", "é"), new Replacement("&ecirc;", "ê"), new Replacement("&euml;", "ë"),
    new Replacement("&igrave;", "ì"), new Replacement("&iacute;", "í"), new Replacement("&icirc;", "î"), new Replacement("&iuml;", "ï"),
    new Replacement("&eth;", "ð"), new Replacement("&ntilde;", "ñ"), new Replacement("&ograve;", "ò"), new Replacement("&oacute;", "ó"),
    new Replacement("&ocirc;", "ô"), new Replacement("&otilde;", "õ"), new Replacement("&ouml;", "ö"), new Replacement("&divide;", "÷"),
    new Replacement("&oslash;", "ø"), new Replacement("&ugrave;", "ù"), new Replacement("&uacute;", "ú"), new Replacement("&ucirc;", "û"),
    new Replacement("&uuml;", "ü"), new Replacement("&yacute;", "ý"), new Replacement("&thorn;", "þ"), new Replacement("&yuml;", "ÿ"),
    new Replacement("&bull;", "•"), new Replacement("&hellip;", "…"), new Replacement("&prime;", "′"), new Replacement("&Prime;", "″"),
    new Replacement("&oline;", "‾"), new Replacement("&frasl;", "⁄"), new Replacement("&weierp;", "℘"), new Replacement("&image;", "ℑ"),
    new Replacement("&real;", "ℜ"), new Replacement("&trade;", "™"), new Replacement("&alefsym;", "ℵ"), new Replacement("&larr;", "←"),
    new Replacement("&uarr;", "↑"), new Replacement("&rarr;", "→"), new Replacement("&darr;", "↓"), new Replacement("&barr;", "↔"),
    new Replacement("&crarr;", "↵"), new Replacement("&lArr;", "⇐"), new Replacement("&uArr;", "⇑"), new Replacement("&rArr;", "⇒"),
    new Replacement("&dArr;", "⇓"), new Replacement("&hArr;", "⇔"), new Replacement("&forall;", "∀"), new Replacement("&part;", "∂"),
    new Replacement("&exist;", "∃"), new Replacement("&empty;", "∅"), new Replacement("&nabla;", "∇"), new Replacement("&isin;", "∈"),
    new Replacement("&notin;", "∉"), new Replacement("&ni;", "∋"), new Replacement("&prod;", "∏"), new Replacement("&sum;", "∑"),
    new Replacement("&minus;", "−"), new Replacement("&lowast", "∗"), new Replacement("&radic;", "√"), new Replacement("&prop;", "∝"),
    new Replacement("&infin;", "∞"), new Replacement("&OEig;", "Œ"), new Replacement("&oelig;", "œ"), new Replacement("&Yuml;", "Ÿ"),
    new Replacement("&spades;", "♠"), new Replacement("&clubs;", "♣"), new Replacement("&hearts;", "♥"), new Replacement("&diams;", "♦"),
    new Replacement("&thetasym;", "ϑ"), new Replacement("&upsih;", "ϒ"), new Replacement("&piv;", "ϖ"), new Replacement("&Scaron;", "Š"),
    new Replacement("&scaron;", "š"), new Replacement("&ang;", "∠"), new Replacement("&and;", "∧"), new Replacement("&or;", "∨"),
    new Replacement("&cap;", "∩"), new Replacement("&cup;", "∪"), new Replacement("&int;", "∫"), new Replacement("&there4;", "∴"),
    new Replacement("&sim;", "∼"), new Replacement("&cong;", "≅"), new Replacement("&asymp;", "≈"), new Replacement("&ne;", "≠"),
    new Replacement("&equiv;", "≡"), new Replacement("&le;", "≤"), new Replacement("&ge;", "≥"), new Replacement("&sub;", "⊂"),
    new Replacement("&sup;", "⊃"), new Replacement("&nsub;", "⊄"), new Replacement("&sube;", "⊆"), new Replacement("&supe;", "⊇"),
    new Replacement("&oplus;", "⊕"), new Replacement("&otimes;", "⊗"), new Replacement("&perp;", "⊥"), new Replacement("&sdot;", "⋅"),
    new Replacement("&lcell;", "⌈"), new Replacement("&rcell;", "⌉"), new Replacement("&lfloor;", "⌊"), new Replacement("&rfloor;", "⌋"),
    new Replacement("&lang;", "⟨"), new Replacement("&rang;", "⟩"), new Replacement("&loz;", "◊"), new Replacement("&uml;", "¨"),
    new Replacement("&lrm;", "")
  )
}
