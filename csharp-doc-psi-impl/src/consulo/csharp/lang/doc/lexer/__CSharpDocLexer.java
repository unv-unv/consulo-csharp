/*
 * Copyright 2013-2017 consulo.io
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/* The following code was generated by JFlex 1.4.4 on 30.03.15 0:21 */

 /* It's an automatically generated code. Do not modify it. */
package consulo.csharp.lang.doc.lexer;

import com.intellij.psi.tree.IElementType;
import com.intellij.lexer.LexerBase;
import consulo.csharp.lang.doc.psi.CSharpDocTokenType;


/**
 * This class is a scanner generated by 
 * <a href="http://www.jflex.de/">JFlex</a> 1.4.4
 * on 30.03.15 0:21 from the specification file
 * <tt>F:/consulo-csharp/csharp-doc-psi-impl/src/org/mustbe/consulo/csharp/lang/doc/lexer/__CSharpDocLexer.flex</tt>
 */
class __CSharpDocLexer extends LexerBase {
  /** initial size of the lookahead buffer */
  private static final int ZZ_BUFFERSIZE = 16384;

  /** lexical states */
  public static final int ATTR_LIST = 8;
  public static final int END_TAG = 4;
  public static final int ATTR_VALUE_DQ = 14;
  public static final int C_COMMENT_END = 20;
  public static final int ATTR = 10;
  public static final int TAG = 2;
  public static final int YYINITIAL = 0;
  public static final int ATTR_VALUE_START = 12;
  public static final int COMMENT = 6;
  public static final int C_COMMENT_START = 18;
  public static final int ATTR_VALUE_SQ = 16;

  /**
   * ZZ_LEXSTATE[l] is the state in the DFA for the lexical state l
   * ZZ_LEXSTATE[l+1] is the state in the DFA for the lexical state l
   *                  at the beginning of a line
   * l is of the form l = 2*k, k a non negative integer
   */
  private static final int ZZ_LEXSTATE[] = { 
     0,  0,  1,  1,  2,  2,  3,  3,  4,  4,  5,  5,  6,  6,  7,  7, 
     8,  8,  9,  9,  9, 9
  };

  /** 
   * Translates characters to character classes
   */
  private static final String ZZ_CMAP_PACKED = 
    "\11\0\2\3\1\0\2\3\22\0\1\3\1\10\1\15\4\0\1\14"+
    "\5\0\1\4\1\2\1\12\12\2\1\5\1\0\1\7\1\13\1\6"+
    "\2\0\32\1\1\11\3\0\1\1\1\0\32\1\57\0\1\1\12\0"+
    "\1\1\4\0\1\1\5\0\27\1\1\0\37\1\1\0\u013f\1\31\0"+
    "\162\1\4\0\14\1\16\0\5\1\11\0\1\1\213\0\1\1\13\0"+
    "\1\1\1\0\3\1\1\0\1\1\1\0\24\1\1\0\54\1\1\0"+
    "\46\1\1\0\5\1\4\0\202\1\10\0\105\1\1\0\46\1\2\0"+
    "\2\1\6\0\20\1\41\0\46\1\2\0\1\1\7\0\47\1\110\0"+
    "\33\1\5\0\3\1\56\0\32\1\5\0\13\1\43\0\2\1\1\0"+
    "\143\1\1\0\1\1\17\0\2\1\7\0\2\1\12\0\3\1\2\0"+
    "\1\1\20\0\1\1\1\0\36\1\35\0\3\1\60\0\46\1\13\0"+
    "\1\1\u0152\0\66\1\3\0\1\1\22\0\1\1\7\0\12\1\43\0"+
    "\10\1\2\0\2\1\2\0\26\1\1\0\7\1\1\0\1\1\3\0"+
    "\4\1\3\0\1\1\36\0\2\1\1\0\3\1\16\0\2\1\23\0"+
    "\6\1\4\0\2\1\2\0\26\1\1\0\7\1\1\0\2\1\1\0"+
    "\2\1\1\0\2\1\37\0\4\1\1\0\1\1\23\0\3\1\20\0"+
    "\11\1\1\0\3\1\1\0\26\1\1\0\7\1\1\0\2\1\1\0"+
    "\5\1\3\0\1\1\22\0\1\1\17\0\2\1\43\0\10\1\2\0"+
    "\2\1\2\0\26\1\1\0\7\1\1\0\2\1\1\0\5\1\3\0"+
    "\1\1\36\0\2\1\1\0\3\1\17\0\1\1\21\0\1\1\1\0"+
    "\6\1\3\0\3\1\1\0\4\1\3\0\2\1\1\0\1\1\1\0"+
    "\2\1\3\0\2\1\3\0\3\1\3\0\10\1\1\0\3\1\113\0"+
    "\10\1\1\0\3\1\1\0\27\1\1\0\12\1\1\0\5\1\46\0"+
    "\2\1\43\0\10\1\1\0\3\1\1\0\27\1\1\0\12\1\1\0"+
    "\5\1\3\0\1\1\40\0\1\1\1\0\2\1\43\0\10\1\1\0"+
    "\3\1\1\0\27\1\1\0\20\1\46\0\2\1\43\0\22\1\3\0"+
    "\30\1\1\0\11\1\1\0\1\1\2\0\7\1\72\0\60\1\1\0"+
    "\2\1\14\0\7\1\72\0\2\1\1\0\1\1\2\0\2\1\1\0"+
    "\1\1\2\0\1\1\6\0\4\1\1\0\7\1\1\0\3\1\1\0"+
    "\1\1\1\0\1\1\2\0\2\1\1\0\4\1\1\0\2\1\11\0"+
    "\1\1\2\0\5\1\1\0\1\1\25\0\2\1\42\0\1\1\77\0"+
    "\10\1\1\0\42\1\35\0\4\1\164\0\42\1\1\0\5\1\1\0"+
    "\2\1\45\0\6\1\112\0\46\1\12\0\51\1\7\0\132\1\5\0"+
    "\104\1\5\0\122\1\6\0\7\1\1\0\77\1\1\0\1\1\1\0"+
    "\4\1\2\0\7\1\1\0\1\1\1\0\4\1\2\0\47\1\1\0"+
    "\1\1\1\0\4\1\2\0\37\1\1\0\1\1\1\0\4\1\2\0"+
    "\7\1\1\0\1\1\1\0\4\1\2\0\7\1\1\0\7\1\1\0"+
    "\27\1\1\0\37\1\1\0\1\1\1\0\4\1\2\0\7\1\1\0"+
    "\47\1\1\0\23\1\105\0\125\1\14\0\u026c\1\2\0\10\1\12\0"+
    "\32\1\5\0\113\1\25\0\15\1\1\0\4\1\16\0\22\1\16\0"+
    "\22\1\16\0\15\1\1\0\3\1\17\0\64\1\43\0\1\1\4\0"+
    "\1\1\103\0\130\1\10\0\51\1\127\0\35\1\63\0\36\1\2\0"+
    "\5\1\u038b\0\154\1\224\0\234\1\4\0\132\1\6\0\26\1\2\0"+
    "\6\1\2\0\46\1\2\0\6\1\2\0\10\1\1\0\1\1\1\0"+
    "\1\1\1\0\1\1\1\0\37\1\2\0\65\1\1\0\7\1\1\0"+
    "\1\1\3\0\3\1\1\0\7\1\3\0\4\1\2\0\6\1\4\0"+
    "\15\1\5\0\3\1\1\0\7\1\164\0\1\1\15\0\1\1\202\0"+
    "\1\1\4\0\1\1\2\0\12\1\1\0\1\1\3\0\5\1\6\0"+
    "\1\1\1\0\1\1\1\0\1\1\1\0\4\1\1\0\3\1\1\0"+
    "\7\1\3\0\3\1\5\0\5\1\u0ebb\0\2\1\52\0\5\1\5\0"+
    "\2\1\4\0\126\1\6\0\3\1\1\0\132\1\1\0\4\1\5\0"+
    "\50\1\4\0\136\1\21\0\30\1\70\0\20\1\u0200\0\u19b6\1\112\0"+
    "\u51a6\1\132\0\u048d\1\u0773\0\u2ba4\1\u215c\0\u012e\1\2\0\73\1\225\0"+
    "\7\1\14\0\5\1\5\0\1\1\1\0\12\1\1\0\15\1\1\0"+
    "\5\1\1\0\1\1\1\0\2\1\1\0\2\1\1\0\154\1\41\0"+
    "\u016b\1\22\0\100\1\2\0\66\1\50\0\14\1\164\0\5\1\1\0"+
    "\207\1\44\0\32\1\6\0\32\1\13\0\131\1\3\0\6\1\2\0"+
    "\6\1\2\0\6\1\2\0\3\1\43\0";

  /** 
   * Translates characters to character classes
   */
  private static final char [] ZZ_CMAP = zzUnpackCMap(ZZ_CMAP_PACKED);

  /** 
   * Translates DFA states to action switch labels.
   */
  private static final int [] ZZ_ACTION = zzUnpackAction();

  private static final String ZZ_ACTION_PACKED_0 =
    "\1\1\11\0\1\1\1\2\1\3\1\4\1\5\1\6"+
    "\1\7\2\4\1\10\1\11\1\12\1\11\1\13\2\14"+
    "\1\15\1\16\1\17\2\20\1\21\3\22\1\0\1\23"+
    "\1\5\1\24\1\10\2\0\1\13\1\0\1\25\1\26";

  private static int [] zzUnpackAction() {
    int [] result = new int[46];
    int offset = 0;
    offset = zzUnpackAction(ZZ_ACTION_PACKED_0, offset, result);
    return result;
  }

  private static int zzUnpackAction(String packed, int offset, int [] result) {
    int i = 0;       /* index in packed string  */
    int j = offset;  /* index in unpacked array */
    int l = packed.length();
    while (i < l) {
      int count = packed.charAt(i++);
      int value = packed.charAt(i++);
      do result[j++] = value; while (--count > 0);
    }
    return j;
  }


  /** 
   * Translates a state to a row index in the transition table
   */
  private static final int [] ZZ_ROWMAP = zzUnpackRowMap();

  private static final String ZZ_ROWMAP_PACKED_0 =
    "\0\0\0\16\0\34\0\52\0\70\0\106\0\124\0\142"+
    "\0\160\0\176\0\214\0\232\0\250\0\266\0\304\0\322"+
    "\0\266\0\340\0\356\0\374\0\266\0\u010a\0\u0118\0\u0126"+
    "\0\266\0\340\0\266\0\266\0\266\0\266\0\340\0\266"+
    "\0\266\0\u0134\0\340\0\u0142\0\266\0\u0150\0\266\0\u015e"+
    "\0\u016c\0\u017a\0\u0188\0\u0196\0\266\0\266";

  private static int [] zzUnpackRowMap() {
    int [] result = new int[46];
    int offset = 0;
    offset = zzUnpackRowMap(ZZ_ROWMAP_PACKED_0, offset, result);
    return result;
  }

  private static int zzUnpackRowMap(String packed, int offset, int [] result) {
    int i = 0;  /* index in packed string  */
    int j = offset;  /* index in unpacked array */
    int l = packed.length();
    while (i < l) {
      int high = packed.charAt(i++) << 16;
      result[j++] = high | packed.charAt(i++);
    }
    return j;
  }

  /** 
   * The transition table of the DFA
   */
  private static final int [] ZZ_TRANS = zzUnpackTrans();

  private static final String ZZ_TRANS_PACKED_0 =
    "\3\13\1\14\3\13\1\15\6\13\1\16\1\17\1\16"+
    "\1\20\2\16\1\21\1\22\2\16\1\23\4\16\1\24"+
    "\1\16\1\20\2\16\1\21\1\22\6\16\4\25\1\26"+
    "\2\25\1\27\6\25\1\16\1\30\1\16\1\20\3\16"+
    "\1\22\6\16\3\31\1\20\3\31\1\32\3\31\1\33"+
    "\1\34\1\35\7\16\1\22\6\16\7\36\1\37\5\36"+
    "\1\40\7\36\1\37\4\36\1\40\1\36\4\41\1\42"+
    "\2\41\1\43\6\41\3\13\1\0\3\13\1\0\6\13"+
    "\3\0\1\14\22\0\1\44\1\0\1\45\22\0\2\17"+
    "\1\0\1\17\1\46\13\0\1\20\22\0\1\44\13\0"+
    "\1\47\10\0\2\24\1\0\1\24\1\50\10\0\4\25"+
    "\1\51\11\25\10\0\1\52\6\0\2\30\1\0\1\30"+
    "\1\53\14\0\1\51\15\0\1\54\12\0\2\46\1\0"+
    "\1\46\12\0\2\50\1\0\1\50\17\0\1\55\20\0"+
    "\1\25\5\0\2\53\1\0\1\53\15\0\1\56\11\0";

  private static int [] zzUnpackTrans() {
    int [] result = new int[420];
    int offset = 0;
    offset = zzUnpackTrans(ZZ_TRANS_PACKED_0, offset, result);
    return result;
  }

  private static int zzUnpackTrans(String packed, int offset, int [] result) {
    int i = 0;       /* index in packed string  */
    int j = offset;  /* index in unpacked array */
    int l = packed.length();
    while (i < l) {
      int count = packed.charAt(i++);
      int value = packed.charAt(i++);
      value--;
      do result[j++] = value; while (--count > 0);
    }
    return j;
  }


  /* error codes */
  private static final int ZZ_UNKNOWN_ERROR = 0;
  private static final int ZZ_NO_MATCH = 1;
  private static final int ZZ_PUSHBACK_2BIG = 2;
  private static final char[] EMPTY_BUFFER = new char[0];
  private static final int YYEOF = -1;

  /* error messages for the codes above */
  private static final String ZZ_ERROR_MSG[] = {
    "Unkown internal scanner error",
    "Error: could not match input",
    "Error: pushback value was too large"
  };

  /**
   * ZZ_ATTRIBUTE[aState] contains the attributes of state <code>aState</code>
   */
  private static final int [] ZZ_ATTRIBUTE = zzUnpackAttribute();

  private static final String ZZ_ATTRIBUTE_PACKED_0 =
    "\1\1\11\0\3\1\1\11\2\1\1\11\3\1\1\11"+
    "\3\1\1\11\1\1\4\11\1\1\2\11\2\1\1\0"+
    "\1\11\1\1\1\11\1\1\2\0\1\1\1\0\2\11";

  private static int [] zzUnpackAttribute() {
    int [] result = new int[46];
    int offset = 0;
    offset = zzUnpackAttribute(ZZ_ATTRIBUTE_PACKED_0, offset, result);
    return result;
  }

  private static int zzUnpackAttribute(String packed, int offset, int [] result) {
    int i = 0;       /* index in packed string  */
    int j = offset;  /* index in unpacked array */
    int l = packed.length();
    while (i < l) {
      int count = packed.charAt(i++);
      int value = packed.charAt(i++);
      do result[j++] = value; while (--count > 0);
    }
    return j;
  }

  /** the current state of the DFA */
  private int zzState;

  /** the current lexical state */
  private int zzLexicalState = YYINITIAL;

  /** this buffer contains the current text to be matched and is
      the source of the yytext() string */
  private CharSequence zzBuffer = "";

  /** the textposition at the last accepting state */
  private int zzMarkedPos;

  /** the textposition at the last state to be included in yytext */
  private int zzPushbackPos;

  /** the current text position in the buffer */
  private int zzCurrentPos;

  /** startRead marks the beginning of the yytext() string in the buffer */
  private int zzStartRead;

  /** endRead marks the last character in the buffer, that has been read
      from input */
  private int zzEndRead;

  /**
   * zzAtBOL == true <=> the scanner is currently at the beginning of a line
   */
  private boolean zzAtBOL = true;

  /** zzAtEOF == true <=> the scanner is at the EOF */
  private boolean zzAtEOF;

  private IElementType myTokenType;
  private int myState;

  /** denotes if the user-EOF-code has already been executed */
  private boolean zzEOFDone;

  /* user code: */
  private int myPrevState = YYINITIAL;

  public int yyprevstate() {
    return myPrevState;
  }

  private int popState(){
    final int prev = myPrevState;
    myPrevState = YYINITIAL;
    return prev;
  }

  protected void pushState(int state){
    myPrevState = state;
  }



  /** 
   * Unpacks the compressed character translation table.
   *
   * @param packed   the packed character translation table
   * @return         the unpacked character translation table
   */
  private static char [] zzUnpackCMap(String packed) {
    char [] map = new char[0x10000];
    int i = 0;  /* index in packed string  */
    int j = 0;  /* index in unpacked array */
    while (i < 1152) {
      int  count = packed.charAt(i++);
      char value = packed.charAt(i++);
      do map[j++] = value; while (--count > 0);
    }
    return map;
  }

  @Override
  public IElementType getTokenType() {
    if (myTokenType == null) locateToken();
    return myTokenType;
  }

  @Override
  public final int getTokenStart(){
    if (myTokenType == null) locateToken();
    return zzStartRead;
  }

  @Override
  public final int getTokenEnd(){
    if (myTokenType == null) locateToken();
    return getTokenStart() + yylength();
  }

  @Override
  public void advance() {
    if (myTokenType == null) locateToken();
    myTokenType = null;
  }

  @Override
  public int getState() {
    if (myTokenType == null) locateToken();
    return myState;
  }

  @Override
  public void start(final CharSequence buffer, int startOffset, int endOffset, final int initialState) {
    reset(buffer, startOffset, endOffset, initialState);
    myTokenType = null;
  }

   @Override
   public CharSequence getBufferSequence() {
     return zzBuffer;
   }

   @Override
   public int getBufferEnd() {
     return zzEndRead;
   }

  public void reset(CharSequence buffer, int start, int end,int initialState){
    zzBuffer = buffer;
    zzCurrentPos = zzMarkedPos = zzStartRead = start;
    zzPushbackPos = 0;
    zzAtEOF  = false;
    zzAtBOL = true;
    zzEndRead = end;
    yybegin(initialState);
    myTokenType = null;
  }

   private void locateToken() {
     if (myTokenType != null) return;
     try {
       myState = yystate();
       myTokenType = advanceImpl();
     }
     catch (java.io.IOException e) { /*Can't happen*/ }
     catch (Error e) {
       // add lexer class name to the error
       final Error error = new Error(getClass().getName() + ": " + e.getMessage());
       error.setStackTrace(e.getStackTrace());
       throw error;
     }
   }

   /**
   * Refills the input buffer.
   *
   * @return      <code>false</code>, iff there was new input.
   *
   * @exception   java.io.IOException  if any I/O-Error occurs
   */
  private boolean zzRefill() throws java.io.IOException {
    return true;
  }


  /**
   * Returns the current lexical state.
   */
  public final int yystate() {
    return zzLexicalState;
  }


  /**
   * Enters a new lexical state
   *
   * @param newState the new lexical state
   */
  public final void yybegin(int newState) {
    zzLexicalState = newState;
  }


  /**
   * Returns the text matched by the current regular expression.
   */
  public final CharSequence yytext() {
    return zzBuffer.subSequence(zzStartRead, zzMarkedPos);
  }


  /**
   * Returns the character at position <tt>pos</tt> from the
   * matched text.
   *
   * It is equivalent to yytext().charAt(pos), but faster
   *
   * @param pos the position of the character to fetch.
   *            A value from 0 to yylength()-1.
   *
   * @return the character at position pos
   */
  public final char yycharat(int pos) {
    return zzBuffer.charAt(zzStartRead+pos);
  }


  /**
   * Returns the length of the matched text region.
   */
  public final int yylength() {
    return zzMarkedPos-zzStartRead;
  }


  /**
   * Reports an error that occured while scanning.
   *
   * In a wellformed scanner (no or only correct usage of
   * yypushback(int) and a match-all fallback rule) this method
   * will only be called with things that "Can't Possibly Happen".
   * If this method is called, something is seriously wrong
   * (e.g. a JFlex bug producing a faulty scanner etc.).
   *
   * Usual syntax/scanner level error handling should be done
   * in error fallback rules.
   *
   * @param   errorCode  the code of the errormessage to display
   */
  private void zzScanError(int errorCode) {
    String message;
    try {
      message = ZZ_ERROR_MSG[errorCode];
    }
    catch (ArrayIndexOutOfBoundsException e) {
      message = ZZ_ERROR_MSG[ZZ_UNKNOWN_ERROR];
    }

    throw new Error(message);
  }


  /**
   * Pushes the specified amount of characters back into the input stream.
   *
   * They will be read again by then next call of the scanning method
   *
   * @param number  the number of characters to be read again.
   *                This number must not be greater than yylength()!
   */
  public void yypushback(int number)  {
    if ( number > yylength() )
      zzScanError(ZZ_PUSHBACK_2BIG);

    zzMarkedPos -= number;
  }


  /**
   * Contains user EOF-code, which will be executed exactly once,
   * when the end of file is reached
   */
  private void zzDoEOF() {
    if (!zzEOFDone) {
      zzEOFDone = true;
    
    }
  }


  /**
   * Resumes scanning until the next regular expression is matched,
   * the end of input is encountered or an I/O-Error occurs.
   *
   * @return      the next token
   * @exception   java.io.IOException  if any I/O-Error occurs
   */
  public IElementType advanceImpl() throws java.io.IOException {
    int zzInput;
    int zzAction;

    // cached fields:
    int zzCurrentPosL;
    int zzMarkedPosL;
    int zzEndReadL = zzEndRead;
    CharSequence zzBufferL = zzBuffer;
    char [] zzCMapL = ZZ_CMAP;

    int [] zzTransL = ZZ_TRANS;
    int [] zzRowMapL = ZZ_ROWMAP;
    int [] zzAttrL = ZZ_ATTRIBUTE;

    while (true) {
      zzMarkedPosL = zzMarkedPos;

      zzAction = -1;

      zzCurrentPosL = zzCurrentPos = zzStartRead = zzMarkedPosL;

      zzState = ZZ_LEXSTATE[zzLexicalState];


      zzForAction: {
        while (true) {

          if (zzCurrentPosL < zzEndReadL)
            zzInput = zzBufferL.charAt(zzCurrentPosL++);
          else if (zzAtEOF) {
            zzInput = YYEOF;
            break zzForAction;
          }
          else {
            // store back cached positions
            zzCurrentPos  = zzCurrentPosL;
            zzMarkedPos   = zzMarkedPosL;
            boolean eof = zzRefill();
            // get translated positions and possibly new buffer
            zzCurrentPosL  = zzCurrentPos;
            zzMarkedPosL   = zzMarkedPos;
            zzBufferL      = zzBuffer;
            zzEndReadL     = zzEndRead;
            if (eof) {
              zzInput = YYEOF;
              break zzForAction;
            }
            else {
              zzInput = zzBufferL.charAt(zzCurrentPosL++);
            }
          }
          int zzNext = zzTransL[ zzRowMapL[zzState] + zzCMapL[zzInput] ];
          if (zzNext == -1) break zzForAction;
          zzState = zzNext;

          int zzAttributes = zzAttrL[zzState];
          if ( (zzAttributes & 1) == 1 ) {
            zzAction = zzState;
            zzMarkedPosL = zzCurrentPosL;
            if ( (zzAttributes & 8) == 8 ) break zzForAction;
          }

        }
      }

      // store back cached position
      zzMarkedPos = zzMarkedPosL;

      switch (zzAction < 0 ? zzAction : ZZ_ACTION[zzAction]) {
        case 1: 
          { return CSharpDocTokenType.XML_DATA_CHARACTERS;
          }
        case 23: break;
        case 17: 
          { yybegin(ATTR_LIST); return CSharpDocTokenType.XML_ATTRIBUTE_VALUE_END_DELIMITER;
          }
        case 24: break;
        case 21: 
          { yybegin(YYINITIAL); return CSharpDocTokenType.XML_COMMENT_END;
          }
        case 25: break;
        case 20: 
          { yybegin(YYINITIAL); return CSharpDocTokenType.XML_EMPTY_ELEMENT_END;
          }
        case 26: break;
        case 8: 
          { return CSharpDocTokenType.XML_NAME;
          }
        case 27: break;
        case 22: 
          { yybegin(COMMENT); return CSharpDocTokenType.XML_COMMENT_START;
          }
        case 28: break;
        case 14: 
          { yybegin(ATTR_VALUE_SQ); return CSharpDocTokenType.XML_ATTRIBUTE_VALUE_START_DELIMITER;
          }
        case 29: break;
        case 16: 
          { return CSharpDocTokenType.XML_ATTRIBUTE_VALUE_TOKEN;
          }
        case 30: break;
        case 12: 
          { yybegin(ATTR_LIST); yypushback(yylength());
          }
        case 31: break;
        case 7: 
          { yybegin(YYINITIAL); return CSharpDocTokenType.XML_TAG_END;
          }
        case 32: break;
        case 10: 
          { return CSharpDocTokenType.XML_BAD_CHARACTER;
          }
        case 33: break;
        case 15: 
          { yybegin(ATTR_VALUE_DQ); return CSharpDocTokenType.XML_ATTRIBUTE_VALUE_START_DELIMITER;
          }
        case 34: break;
        case 6: 
          { return CSharpDocTokenType.TAG_WHITE_SPACE;
          }
        case 35: break;
        case 5: 
          { yybegin(ATTR_LIST); pushState(TAG); return CSharpDocTokenType.XML_NAME;
          }
        case 36: break;
        case 2: 
          { return CSharpDocTokenType.XML_REAL_WHITE_SPACE;
          }
        case 37: break;
        case 18: 
          { yybegin(COMMENT); return CSharpDocTokenType.XML_COMMENT_CHARACTERS;
          }
        case 38: break;
        case 11: 
          { yybegin(ATTR); return CSharpDocTokenType.XML_NAME;
          }
        case 39: break;
        case 3: 
          { yybegin(TAG); return CSharpDocTokenType.XML_START_TAG_START;
          }
        case 40: break;
        case 13: 
          { return CSharpDocTokenType.XML_EQ;
          }
        case 41: break;
        case 4: 
          { if(yystate() == YYINITIAL){
        return CSharpDocTokenType.XML_BAD_CHARACTER;
      }
      else yybegin(popState()); yypushback(yylength());
          }
        case 42: break;
        case 9: 
          { return CSharpDocTokenType.XML_COMMENT_CHARACTERS;
          }
        case 43: break;
        case 19: 
          { yybegin(END_TAG); return CSharpDocTokenType.XML_END_TAG_START;
          }
        case 44: break;
        default:
          if (zzInput == YYEOF && zzStartRead == zzCurrentPos) {
            zzAtEOF = true;
            zzDoEOF();
            return null;
          }
          else {
            zzScanError(ZZ_NO_MATCH);
          }
      }
    }
  }


}
