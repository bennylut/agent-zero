/* 
 * The MIT License
 *
 * Copyright 2016 Benny Lutati.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package bgu.dcr.az.dev.ui;

import bc.ui.swing.models.LimitedBatchDocument;
import java.awt.Color;
import java.util.AbstractMap.SimpleEntry;
import java.util.Formatter;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.JTextPane;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

/**
 *
 * @author bennyl
 */
public class AgentLogDocument extends LimitedBatchDocument {
    public static final String LEFT_JUSTIFIED_SPACED_FORMAT = "%-18s";
    public static final String REPEAT_INDICATOR_STRING = "#";
    private static final Color AGENT_NAME_BACKGROUND = new Color(51,51,51);
    private static final Color NORMAL_TEXT_COLOR = new Color(153,255,153);

    private String lastAgent = "";
    private HashMap<String, SimpleAttributeSet> AttributeSets;
    private Color[] colors;
    StringBuilder tokenBuilder = new StringBuilder();
    private Formatter formatter = new Formatter(tokenBuilder);

    private Matcher match=null;
    
    
    public AgentLogDocument(JTextPane container) {
        super(container);
        this.AttributeSets = new HashMap<String, SimpleAttributeSet>();
        generateColors();

    }

    
    public SimpleEntry<Integer,Integer> search(String what,boolean ragex,int offset){
            if(!ragex){
                what=Pattern.quote(what);
                
            }
            Pattern p =(Pattern.compile(what ,Pattern.CASE_INSENSITIVE ));
            
            try {
                match=p.matcher(getText(offset, getLength()-offset));
            } catch (BadLocationException ex) {
                Logger.getLogger(AgentLogDocument.class.getName()).log(Level.SEVERE, null, ex);
                return new SimpleEntry<Integer,Integer>(-1,-1);
            }
            return (match.find()? new SimpleEntry<Integer,Integer>(match.start(),match.end()):new SimpleEntry<Integer,Integer>(-1,-1));
    }
    
    private String createToken(String format, Object... args) {
        tokenBuilder.setLength(0);
        formatter.format(format, args);
        return tokenBuilder.toString();
    }

    public synchronized void addLog(String agentName, String text, Level lvl) {
        for (String t : text.split("\n")){
            addSingleLineLog(agentName, " " + t, lvl);
        }
    }
    
    private synchronized void addSingleLineLog(String agentName, String text, Level lvl){
        addColor(agentName);


        if (lastAgent.equals(agentName)) {
            String newAgentName = createToken(LEFT_JUSTIFIED_SPACED_FORMAT, REPEAT_INDICATOR_STRING);
            appendBatchString(newAgentName, styleRepeatSpacing(agentName));

        } else {
            String newAgentName = createToken(LEFT_JUSTIFIED_SPACED_FORMAT, agentName + ":");
            appendBatchString(newAgentName, styleAgentName(agentName));
            lastAgent = agentName;
        }

        
        //StringBuilder sb = parseLogText(text);
        appendBatchString(text+"\n", styleText(lvl));
//        appendBatchLineFeed(null);
    }

    private void generateColors() {

        colors = new Color[]{
            new Color(15,108,248),
            new Color(152,203,50),
            new Color(217,27,153),
            new Color(255,72,0),
            new Color(19,238,189),
            new Color(228,0,249),
            new Color(105, 251, 44),
            new Color(255,225,25),
            new Color(53,153,255),
            new Color(0,238,118),
            new Color(132, 132, 242),
            new Color(255, 255, 153),
            new Color(255, 148, 47),
            new Color(0, 247, 105),
            new Color(255, 38, 109),
            new Color(53, 197, 208),
            new Color(153, 255, 153),
            new Color(240, 71, 71),
            new Color(153, 153, 255),
            new Color(170, 170, 170),
            Color.BLACK,
            new Color(255, 0, 0),
            new Color(255, 204, 0)};




    }

    private AttributeSet styleRepeatSpacing(String agentName) {
        SimpleAttributeSet ans = this.AttributeSets.get(agentName);
        StyleConstants.setUnderline(ans, false);
        return ans;

    }

    private AttributeSet styleAgentName(String agentName) {
        SimpleAttributeSet ans = this.AttributeSets.get(agentName);
        StyleConstants.setUnderline(ans, false);
        StyleConstants.setBackground(ans, AGENT_NAME_BACKGROUND);

//        StyleConstants.setUnderline(ans, true);
        return ans;

    }

    private AttributeSet styleText(Level lvl) {
        SimpleAttributeSet textColor = new SimpleAttributeSet();
        if (lvl.equals(Level.SEVERE)) {
            StyleConstants.ColorConstants.setForeground(textColor, colors[21]);
        } else if (lvl.equals(Level.WARNING)) {
            StyleConstants.ColorConstants.setForeground(textColor, colors[22]);
        } else {
            StyleConstants.ColorConstants.setForeground(textColor, NORMAL_TEXT_COLOR);
        }

        return textColor;
    }

    private void addColor(String agentName) {

        if (AttributeSets.containsKey(agentName)) {

            return;

        }

        int index = this.AttributeSets.size();

        index = index % 20;

        Color tmp = colors[index];

        SimpleAttributeSet ats = new SimpleAttributeSet();

        StyleConstants.ColorConstants.setForeground(ats, tmp);

        AttributeSets.put(agentName, ats);

    }
}
