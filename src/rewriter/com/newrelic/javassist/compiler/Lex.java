/*     */ package com.newrelic.javassist.compiler;
/*     */ 
/*     */ public class Lex
/*     */   implements TokenId
/*     */ {
/*     */   private int lastChar;
/*     */   private StringBuffer textBuffer;
/*     */   private Token currentToken;
/*     */   private Token lookAheadTokens;
/*     */   private String input;
/*     */   private int position;
/*     */   private int maxlen;
/*     */   private int lineNumber;
/* 354 */   private static final int[] equalOps = { 350, 0, 0, 0, 351, 352, 0, 0, 0, 353, 354, 0, 355, 0, 356, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 357, 358, 359, 0 };
/*     */ 
/* 457 */   private static final KeywordTable ktable = new KeywordTable();
/*     */ 
/*     */   public Lex(String s)
/*     */   {
/*  40 */     this.lastChar = -1;
/*  41 */     this.textBuffer = new StringBuffer();
/*  42 */     this.currentToken = new Token();
/*  43 */     this.lookAheadTokens = null;
/*     */ 
/*  45 */     this.input = s;
/*  46 */     this.position = 0;
/*  47 */     this.maxlen = s.length();
/*  48 */     this.lineNumber = 0;
/*     */   }
/*     */ 
/*     */   public int get() {
/*  52 */     if (this.lookAheadTokens == null)
/*  53 */       return get(this.currentToken);
/*     */     Token t;
/*  56 */     this.currentToken = (t = this.lookAheadTokens);
/*  57 */     this.lookAheadTokens = this.lookAheadTokens.next;
/*  58 */     return t.tokenId;
/*     */   }
/*     */ 
/*     */   public int lookAhead()
/*     */   {
/*  66 */     return lookAhead(0);
/*     */   }
/*     */ 
/*     */   public int lookAhead(int i) {
/*  70 */     Token tk = this.lookAheadTokens;
/*  71 */     if (tk == null) {
/*  72 */       this.lookAheadTokens = (tk = this.currentToken);
/*  73 */       tk.next = null;
/*  74 */       get(tk);
/*     */     }
/*     */ 
/*  77 */     for (; i-- > 0; tk = tk.next) {
/*  78 */       if (tk.next == null)
/*     */       {
/*     */         Token tk2;
/*  80 */         tk.next = (tk2 = new Token());
/*  81 */         get(tk2);
/*     */       }
/*     */     }
/*  84 */     this.currentToken = tk;
/*  85 */     return tk.tokenId;
/*     */   }
/*     */ 
/*     */   public String getString() {
/*  89 */     return this.currentToken.textValue;
/*     */   }
/*     */ 
/*     */   public long getLong() {
/*  93 */     return this.currentToken.longValue;
/*     */   }
/*     */ 
/*     */   public double getDouble() {
/*  97 */     return this.currentToken.doubleValue;
/*     */   }
/*     */ 
/*     */   private int get(Token token) {
/*     */     int t;
/*     */     do
/* 103 */       t = readLine(token);
/* 104 */     while (t == 10);
/* 105 */     token.tokenId = t;
/* 106 */     return t;
/*     */   }
/*     */ 
/*     */   private int readLine(Token token) {
/* 110 */     int c = getNextNonWhiteChar();
/* 111 */     if (c < 0)
/* 112 */       return c;
/* 113 */     if (c == 10) {
/* 114 */       this.lineNumber += 1;
/* 115 */       return 10;
/*     */     }
/* 117 */     if (c == 39)
/* 118 */       return readCharConst(token);
/* 119 */     if (c == 34)
/* 120 */       return readStringL(token);
/* 121 */     if ((48 <= c) && (c <= 57))
/* 122 */       return readNumber(c, token);
/* 123 */     if (c == 46) {
/* 124 */       c = getc();
/* 125 */       if ((48 <= c) && (c <= 57)) {
/* 126 */         StringBuffer tbuf = this.textBuffer;
/* 127 */         tbuf.setLength(0);
/* 128 */         tbuf.append('.');
/* 129 */         return readDouble(tbuf, c, token);
/*     */       }
/*     */ 
/* 132 */       ungetc(c);
/* 133 */       return readSeparator(46);
/*     */     }
/*     */ 
/* 136 */     if (Character.isJavaIdentifierStart((char)c)) {
/* 137 */       return readIdentifier(c, token);
/*     */     }
/* 139 */     return readSeparator(c);
/*     */   }
/*     */ 
/*     */   private int getNextNonWhiteChar() {
/*     */     int c;
/*     */     do {
/* 145 */       c = getc();
/* 146 */       if (c == 47) {
/* 147 */         c = getc();
/* 148 */         if (c == 47) {
/*     */           do {
/* 150 */             c = getc();
/* 151 */             if ((c == 10) || (c == 13)) break;  } while (c != -1); } else {
/* 152 */           if (c == 42)
/*     */             while (true) {
/* 154 */               c = getc();
/* 155 */               if (c == -1)
/*     */                 break;
/* 157 */               if (c == 42) {
/* 158 */                 if ((c = getc()) == 47) {
/* 159 */                   c = 32;
/* 160 */                   break;
/*     */                 }
/*     */ 
/* 163 */                 ungetc(c);
/*     */               }
/*     */             }
/* 166 */           ungetc(c);
/* 167 */           c = 47;
/*     */         }
/*     */       }
/*     */     }
/* 170 */     while (isBlank(c));
/* 171 */     return c;
/*     */   }
/*     */ 
/*     */   private int readCharConst(Token token)
/*     */   {
/* 176 */     int value = 0;
/*     */     int c;
/* 177 */     while ((c = getc()) != 39)
/* 178 */       if (c == 92) {
/* 179 */         value = readEscapeChar(); } else {
/* 180 */         if (c < 32) {
/* 181 */           if (c == 10) {
/* 182 */             this.lineNumber += 1;
/*     */           }
/* 184 */           return 500;
/*     */         }
/*     */ 
/* 187 */         value = c;
/*     */       }
/* 189 */     token.longValue = value;
/* 190 */     return 401;
/*     */   }
/*     */ 
/*     */   private int readEscapeChar() {
/* 194 */     int c = getc();
/* 195 */     if (c == 110)
/* 196 */       c = 10;
/* 197 */     else if (c == 116)
/* 198 */       c = 9;
/* 199 */     else if (c == 114)
/* 200 */       c = 13;
/* 201 */     else if (c == 102)
/* 202 */       c = 12;
/* 203 */     else if (c == 10) {
/* 204 */       this.lineNumber += 1;
/*     */     }
/* 206 */     return c;
/*     */   }
/*     */   private int readStringL(Token token) {
/* 211 */     StringBuffer tbuf = this.textBuffer;
/* 212 */     tbuf.setLength(0);
/*     */     int c;
/*     */     do { while ((c = getc()) != 34) {
/* 215 */         if (c == 92) {
/* 216 */           c = readEscapeChar();
/* 217 */         } else if ((c == 10) || (c < 0)) {
/* 218 */           this.lineNumber += 1;
/* 219 */           return 500;
/*     */         }
/*     */ 
/* 222 */         tbuf.append((char)c);
/*     */       }
/*     */       while (true)
/*     */       {
/* 226 */         c = getc();
/* 227 */         if (c == 10)
/* 228 */           this.lineNumber += 1;
/* 229 */         else if (!isBlank(c))
/* 230 */           break;
/*     */       }
/*     */     }
/* 233 */     while (c == 34);
/* 234 */     ungetc(c);
/*     */ 
/* 239 */     token.textValue = tbuf.toString();
/* 240 */     return 406;
/*     */   }
/*     */ 
/*     */   private int readNumber(int c, Token token) {
/* 244 */     long value = 0L;
/* 245 */     int c2 = getc();
/* 246 */     if (c == 48) {
/* 247 */       if ((c2 == 88) || (c2 == 120)) {
/*     */         while (true) {
/* 249 */           c = getc();
/* 250 */           if ((48 <= c) && (c <= 57)) {
/* 251 */             value = value * 16L + (c - 48);
/* 252 */           } else if ((65 <= c) && (c <= 70)) {
/* 253 */             value = value * 16L + (c - 65 + 10); } else {
/* 254 */             if ((97 > c) || (c > 102)) break;
/* 255 */             value = value * 16L + (c - 97 + 10);
/*     */           }
/*     */         }
/* 257 */         token.longValue = value;
/* 258 */         if ((c == 76) || (c == 108)) {
/* 259 */           return 403;
/*     */         }
/* 261 */         ungetc(c);
/* 262 */         return 402;
/*     */       }
/*     */ 
/* 266 */       if ((48 <= c2) && (c2 <= 55)) {
/* 267 */         value = c2 - 48;
/*     */         while (true) {
/* 269 */           c = getc();
/* 270 */           if ((48 > c) || (c > 55)) break;
/* 271 */           value = value * 8L + (c - 48);
/*     */         }
/* 273 */         token.longValue = value;
/* 274 */         if ((c == 76) || (c == 108)) {
/* 275 */           return 403;
/*     */         }
/* 277 */         ungetc(c);
/* 278 */         return 402;
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 284 */     value = c - 48;
/* 285 */     while ((48 <= c2) && (c2 <= 57)) {
/* 286 */       value = value * 10L + c2 - 48L;
/* 287 */       c2 = getc();
/*     */     }
/*     */ 
/* 290 */     token.longValue = value;
/* 291 */     if ((c2 == 70) || (c2 == 102)) {
/* 292 */       token.doubleValue = value;
/* 293 */       return 404;
/*     */     }
/* 295 */     if ((c2 == 69) || (c2 == 101) || (c2 == 68) || (c2 == 100) || (c2 == 46))
/*     */     {
/* 297 */       StringBuffer tbuf = this.textBuffer;
/* 298 */       tbuf.setLength(0);
/* 299 */       tbuf.append(value);
/* 300 */       return readDouble(tbuf, c2, token);
/*     */     }
/* 302 */     if ((c2 == 76) || (c2 == 108)) {
/* 303 */       return 403;
/*     */     }
/* 305 */     ungetc(c2);
/* 306 */     return 402;
/*     */   }
/*     */ 
/*     */   private int readDouble(StringBuffer sbuf, int c, Token token)
/*     */   {
/* 311 */     if ((c != 69) && (c != 101) && (c != 68) && (c != 100)) {
/* 312 */       sbuf.append((char)c);
/*     */       while (true) {
/* 314 */         c = getc();
/* 315 */         if ((48 > c) || (c > 57)) break;
/* 316 */         sbuf.append((char)c);
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 322 */     if ((c == 69) || (c == 101)) {
/* 323 */       sbuf.append((char)c);
/* 324 */       c = getc();
/* 325 */       if ((c == 43) || (c == 45)) {
/* 326 */         sbuf.append((char)c);
/* 327 */         c = getc();
/*     */       }
/*     */ 
/* 330 */       while ((48 <= c) && (c <= 57)) {
/* 331 */         sbuf.append((char)c);
/* 332 */         c = getc();
/*     */       }
/*     */     }
/*     */     try
/*     */     {
/* 337 */       token.doubleValue = Double.parseDouble(sbuf.toString());
/*     */     }
/*     */     catch (NumberFormatException e) {
/* 340 */       return 500;
/*     */     }
/*     */ 
/* 343 */     if ((c == 70) || (c == 102)) {
/* 344 */       return 404;
/*     */     }
/* 346 */     if ((c != 68) && (c != 100)) {
/* 347 */       ungetc(c);
/*     */     }
/* 349 */     return 405;
/*     */   }
/*     */ 
/*     */   private int readSeparator(int c)
/*     */   {
/* 362 */     if ((33 <= c) && (c <= 63)) {
/* 363 */       int t = equalOps[(c - 33)];
/* 364 */       if (t == 0) {
/* 365 */         return c;
/*     */       }
/* 367 */       int c2 = getc();
/* 368 */       if (c == c2)
/*     */       {
/*     */         int c3;
/* 369 */         switch (c) {
/*     */         case 61:
/* 371 */           return 358;
/*     */         case 43:
/* 373 */           return 362;
/*     */         case 45:
/* 375 */           return 363;
/*     */         case 38:
/* 377 */           return 369;
/*     */         case 60:
/* 379 */           c3 = getc();
/* 380 */           if (c3 == 61) {
/* 381 */             return 365;
/*     */           }
/* 383 */           ungetc(c3);
/* 384 */           return 364;
/*     */         case 62:
/* 387 */           c3 = getc();
/* 388 */           if (c3 == 61)
/* 389 */             return 367;
/* 390 */           if (c3 == 62) {
/* 391 */             c3 = getc();
/* 392 */             if (c3 == 61) {
/* 393 */               return 371;
/*     */             }
/* 395 */             ungetc(c3);
/* 396 */             return 370;
/*     */           }
/*     */ 
/* 400 */           ungetc(c3);
/* 401 */           return 366;
/*     */         }
/*     */ 
/*     */       }
/* 406 */       else if (c2 == 61) {
/* 407 */         return t;
/*     */       }
/*     */     }
/* 410 */     else if (c == 94) {
/* 411 */       int c2 = getc();
/* 412 */       if (c2 == 61)
/* 413 */         return 360;
/*     */     }
/* 415 */     else if (c == 124) {
/* 416 */       int c2 = getc();
/* 417 */       if (c2 == 61)
/* 418 */         return 361;
/* 419 */       if (c2 == 124)
/* 420 */         return 368;
/*     */     }
/*     */     else {
/* 423 */       return c;
/*     */     }
/*     */     int c2;
/* 425 */     ungetc(c2);
/* 426 */     return c;
/*     */   }
/*     */ 
/*     */   private int readIdentifier(int c, Token token) {
/* 430 */     StringBuffer tbuf = this.textBuffer;
/* 431 */     tbuf.setLength(0);
/*     */     do
/*     */     {
/* 434 */       tbuf.append((char)c);
/* 435 */       c = getc();
/* 436 */     }while (Character.isJavaIdentifierPart((char)c));
/*     */ 
/* 438 */     ungetc(c);
/*     */ 
/* 440 */     String name = tbuf.toString();
/* 441 */     int t = ktable.lookup(name);
/* 442 */     if (t >= 0) {
/* 443 */       return t;
/*     */     }
/*     */ 
/* 452 */     token.textValue = name;
/* 453 */     return 400;
/*     */   }
/*     */ 
/*     */   private static boolean isBlank(int c)
/*     */   {
/* 514 */     return (c == 32) || (c == 9) || (c == 12) || (c == 13) || (c == 10);
/*     */   }
/*     */ 
/*     */   private static boolean isDigit(int c)
/*     */   {
/* 519 */     return (48 <= c) && (c <= 57);
/*     */   }
/*     */ 
/*     */   private void ungetc(int c) {
/* 523 */     this.lastChar = c;
/*     */   }
/*     */ 
/*     */   public String getTextAround() {
/* 527 */     int begin = this.position - 10;
/* 528 */     if (begin < 0) {
/* 529 */       begin = 0;
/*     */     }
/* 531 */     int end = this.position + 10;
/* 532 */     if (end > this.maxlen) {
/* 533 */       end = this.maxlen;
/*     */     }
/* 535 */     return this.input.substring(begin, end);
/*     */   }
/*     */ 
/*     */   private int getc() {
/* 539 */     if (this.lastChar < 0) {
/* 540 */       if (this.position < this.maxlen) {
/* 541 */         return this.input.charAt(this.position++);
/*     */       }
/* 543 */       return -1;
/*     */     }
/* 545 */     int c = this.lastChar;
/* 546 */     this.lastChar = -1;
/* 547 */     return c;
/*     */   }
/*     */ 
/*     */   static
/*     */   {
/* 460 */     ktable.append("abstract", 300);
/* 461 */     ktable.append("boolean", 301);
/* 462 */     ktable.append("break", 302);
/* 463 */     ktable.append("byte", 303);
/* 464 */     ktable.append("case", 304);
/* 465 */     ktable.append("catch", 305);
/* 466 */     ktable.append("char", 306);
/* 467 */     ktable.append("class", 307);
/* 468 */     ktable.append("const", 308);
/* 469 */     ktable.append("continue", 309);
/* 470 */     ktable.append("default", 310);
/* 471 */     ktable.append("do", 311);
/* 472 */     ktable.append("double", 312);
/* 473 */     ktable.append("else", 313);
/* 474 */     ktable.append("extends", 314);
/* 475 */     ktable.append("false", 411);
/* 476 */     ktable.append("final", 315);
/* 477 */     ktable.append("finally", 316);
/* 478 */     ktable.append("float", 317);
/* 479 */     ktable.append("for", 318);
/* 480 */     ktable.append("goto", 319);
/* 481 */     ktable.append("if", 320);
/* 482 */     ktable.append("implements", 321);
/* 483 */     ktable.append("import", 322);
/* 484 */     ktable.append("instanceof", 323);
/* 485 */     ktable.append("int", 324);
/* 486 */     ktable.append("interface", 325);
/* 487 */     ktable.append("long", 326);
/* 488 */     ktable.append("native", 327);
/* 489 */     ktable.append("new", 328);
/* 490 */     ktable.append("null", 412);
/* 491 */     ktable.append("package", 329);
/* 492 */     ktable.append("private", 330);
/* 493 */     ktable.append("protected", 331);
/* 494 */     ktable.append("public", 332);
/* 495 */     ktable.append("return", 333);
/* 496 */     ktable.append("short", 334);
/* 497 */     ktable.append("static", 335);
/* 498 */     ktable.append("strictfp", 347);
/* 499 */     ktable.append("super", 336);
/* 500 */     ktable.append("switch", 337);
/* 501 */     ktable.append("synchronized", 338);
/* 502 */     ktable.append("this", 339);
/* 503 */     ktable.append("throw", 340);
/* 504 */     ktable.append("throws", 341);
/* 505 */     ktable.append("transient", 342);
/* 506 */     ktable.append("true", 410);
/* 507 */     ktable.append("try", 343);
/* 508 */     ktable.append("void", 344);
/* 509 */     ktable.append("volatile", 345);
/* 510 */     ktable.append("while", 346);
/*     */   }
/*     */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.javassist.compiler.Lex
 * JD-Core Version:    0.6.2
 */