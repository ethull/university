package vmtranslator;

import java.io.IOException;
import java.io.Writer;

/**
 * Output Hack ASM for VM code.
 * 
 * @author ethull
 */
public class CodeWriter {
    // Temporary registers for intermediate calculations,
    // should they be required by the translator.
    private static final String R13 = "R13", R14 = "R14", R15 = "R15";
    
    // Where the translation is written.
    private final Writer writer;
    // Current file being processed.
    private String currentFilename;
    
    //count used for jump identifiers
    private int jmpCount = 0;

    //inc count for the next jump identifier
    private String nextJmpCount(){
        //makes sense to start from 0
        String prevCount = Integer.toString(jmpCount);
        jmpCount++;
        return  prevCount;
    }
    
    /**
     * Create a CodeWriter to output to the given file.
     * @param writer Where to write the code.
     */
    public CodeWriter(Writer writer){
        this.writer = writer;
    }
    
    /**
     * Translation of a new file.
     * @param filename The input file name.
     */
    public void setFilename(String filename){
        this.currentFilename = filename;
    }

    /**
     * Translate the given arithmetic command.
     * @param command The command to be translated.
     * @throws java.io.IOException
     */
    public void writeArithmetic(String command)
            throws IOException
    {
        switch(command){
            // Binary arithmetic operators.
            case "add":
                output(getArithStr("+"));
                break;
            case "and":
                output(getArithStr("&"));
                break;
            case "or":
                output(getArithStr("|"));
                break;
            case "sub":
                output(getArithStr("-"));
                break;
                
            // Unary operators.
            case "neg":
                output(getUnaryNeg());
                break;
            case "not":
                output(getUnaryNot());
                break;
                
            // Relational operators.
            case "eq":
                //= JEQ
                output(getRelationalStr("JEQ"));
                break;
            case "lt":
                //< JLT
                output(getRelationalStr("JLT"));
                break;
            case "gt":
                //> JGT
                output(getRelationalStr("JGT"));
                break;
            default:
                throw new IllegalStateException("Unrecognised arithmetic command: " + command);
        }        
    }

    //arithmetic operations: + & | -
    //pop two values off the stack and push result
    //  assumes both target values are on the main stack
    public String getArithStr(String symbol){
        return
            "@SP\n" + //A=SP, M=*SP
            "AM=M-1\n" + //mv stack pointer back one
            "D=M\n" +
            //the second value is not popped, instead its replaced with the result
            "A=A-1\n" +
            //perform op, save to mem a, secondPushedVal=secondPushedVal<symbol>firstPushedVal
            "M=M"+symbol+"D\n";
    }

    //pop the rhs, then the lhs, then evaluate
    public String getRelationalStr(String jumpType){
        String key = nextJmpCount();
        return 

            "@SP\n" +
            "AM=M-1\n" +
            "D=M\n" +
            "A=A-1\n" +
            "D=M-D\n" +
            //if (lhs-rhs) <cond> 0 then jump to if code block (JXX.true.key)
            //  EG 3 4 lt (3<4) -> 3-4 JLT (<0) -> -1<0 hence jump
            "@" + jumpType + ".TRUE." + key + "\n" +
            "D;" + jumpType + "\n" +
            //if else no match continue execution
            //  set topOfStack to 0
            //  then jump past the if code block, (to (JXX.after.key))
            "@SP\n" +
            "A=M-1\n" +
            "M=0\n" +
            "@" + jumpType + ".SKIP." + key + "\n" +
            "0;JMP\n" +
            "(" + jumpType + ".TRUE." + key + ")\n" +
            //if match set topOfStack to -1 (effectivelly pushing eval result)
            "@SP\n" +
            "A=M-1\n" +
            "M=-1\n" +
            "(" + jumpType + ".SKIP." + key + ")\n";
    }

    //binary not
    public String getUnaryNot(){
        //flip true/false polarity of last item on stack
        //0 -> 1, x -> 0, where x is anything but 0
        return
            "@SP\n" +
            //the last value is at addr *SP-1
            "A=M-1\n" + 
            //! means not in hack
            "M=!M\n";
    }

    //binary negate, flip the sign, by taking away from 0
    public String getUnaryNeg(){
        return
            "D=0\n" +
            "@SP\n" +
            "A=M-1\n" +
            "M=D-M\n";
            /* possible alternative implementation:
            "@SP\n" +
            "A=M-1\n" +
            "M=-M\n";
            */
    }

    /**
     * Translate the given push or pop command.
     * @param command The command to be translated.
     * @param segment The segment to be accessed.
     * @param index   The index within segment.
     * @throws java.io.IOException
     */
    public void writePushPop(CommandType command, String segment, int index) 
            throws IOException
    {
        if(null == command){
            throw new IllegalStateException("Invalid command in writePushPop: " +
                    command);
        }
        else {
            switch (command){
                case C_PUSH:                    
                    switch(segment){
                        case "local":
                            output(pushLATT("LCL", index));
                            break;
                        case "argument":
                            output(pushLATT("ARG", index));
                            break;
                        case "this":
                            output(pushLATT("THIS", index));
                            break;
                        case "that":
                            output(pushLATT("THAT", index));
                            break;
                        case "constant":
                            //push constant 10, *SP=i, SP++
                            //needs own mtd since no pointer to add to index
                            output(pushC(index));
                            break;
                        case "temp":
                            //push temp 10, addr=5+i, *SP=*addr, SP++
                            //no default @ key, stored between R5 and R12
                            output(pushLATT("R5", index+5));
                            break;
                        case "pointer":
                            //*SP = THIS/THAT, SP++
                            //index can be 0 or 1 if its a pointer
                            if (index == 0){
                                output(pushP("THIS"));
                            }else if (index  == 1) {
                                output(pushP("THAT"));
                            }else{
                                throw new IllegalStateException("Invalid pointer index in writePushPop: " + segment);
                            }
                            break;
                        case "static":
                            //alternative mtd
                            //static vars stored from addr 16
                            output(pushS(index));
                            //output(pushP(Integer((index + 16)).toString()));

                            break;
                        default:
                            throw new IllegalStateException("Invalid segment in writePushPop: " + segment);
                    }
                    break;
                case C_POP:
                    switch(segment){
                        case "local":
                            output(popLATT("LCL", index));
                            break;
                        case "argument":
                            output(popLATT("ARG", index));
                            break;
                        case "this":
                            output(popLATT("THIS", index));
                            break;
                        case "that":
                            output(popLATT("THAT", index));
                            break;
                        case "temp":
                            //push temp 10, addr=5+i, *SP=*addr, SP++
                            //no default @ key, stored between R5 and R12
                            output(popLATT("R5", index+5));
                            break;
                        case "pointer":
                            //*SP = THIS/THAT, SP++
                            //index can be 0 or 1 if its a pointer
                            if (index == 0){
                                output(popP("THIS"));
                            }else {
                                output(popP("THAT"));
                            }
                            break;
                        case "static":
                            output(popS(index));

                            //alternative mtd, but doesn't look good as asm
                            //static vars stored from addr 16
                            //output(pushP(Integer((index+16)).toString()));
                            break;
                        default:
                            throw new IllegalStateException("Invalid segment in writePushPop: " + segment);
                    }
                    break;
                default:
                    throw new IllegalStateException("Invalid command in writePushPop: " +
                            command);
            }
        }
    }

    //need to go get value from target segment, then push it
    //  works for local/arg/this/that segments, it will also work with temp if index is incremented by 5
    public String pushLATT(String sgmt, int index){
        return 
            //get val in the segment pointer (addr of segment) and store to D
            "@" + sgmt + "\n" + 
            "D=M\n" + //D=*sgmt
            //add index and sgmt addr, then data at combined addr location to D
            "@" + index + "\n" + //set A register to index, sets M=RAM[index]
            "A=D+A\n" + //A=*sgmt + A
            "D=M\n" + //D=RAM[A] (RAM[*sgmt+index]), D is set to the val to be pushed (*(*sgmt+index))

            push(); //push D to stack
    }

    //push a constant, doesnt require a segment pointer
    public String pushC(int index){
        return 
            "@" + index + "\n" +
            //have to store from A, since getting value from the addrs name rather than what it stores
            "D=A\n" +
            push();
    }

    //push a pointer (direct access)
    public String pushP(String sgmt){
        //pointer sgmt stores base addrs of THIS and THAT

        //since directly pushing addrs stored in THIS/THAT, dont need to follow any segment pointers (A is not needed)
        //since directly passing correct addr and we dont increment a segment pointer, dont need an index
        return
            "@" + sgmt + "\n" + 
            "D=M\n" +
            push();
    }

    //push a static var (direct access)
    public String pushS(int index){
        return
        "@" + currentFilename + "." + index + "\n" +
        "D=M\n" +
        push();
    }

    //push value onto the stack, preceding string must finish by storing a value to D
    //general stack push: add value -> increase pointer by one
    //hack stack push aim: add value from a segment to the main stack
    //EG push segment 1: goto segment one, and push to main stack
    public String push(){
        return
            //push D
            //*SP=D
            "@SP\n" + //A=SP (addr of stack pointer (@0)), RAM[A]=*SP
            "A=M\n" + //A=*SP (current addr pointed to by stack pointer)
            "M=D\n" + //RAM[A]=valueToPush, the stack pointer is at the current number + 1, so we first need to set *SP to new value
            //SP++
            "@SP\n" +
            "M=M+1\n"; //RAM[A]=RAM[A]+1 (*SP=*SP+1), inc stack pointer
    }
   
    //set D to the addr we want to pop to, and then call pop()
    public String popLATT(String sgmt, int index){
        return
            //get val in the segment pointer (addr of segment) and store to D
            "@" + sgmt + "\n" +
            "D=M\n" + //D=*sgmt

            "@" + index + "\n" + //set A register to index, sets M=RAM[index]
            "D=D+A\n" + //D=*sgmt + index, D stores addr of valueToReplace (unlike push, saves to D and not A since we want an addr rather than the value)
            pop(); //pop from stack to addr in D
    }
    //pop a pointer
    public String popP(String sgmt){
        return
            "@" + sgmt + "\n" +
            "D=A\n" +
            pop();
    }
    //pop static var
    public String popS(int index){
        return
            "@" + currentFilename + "." + index + "\n" +
            "D=A\n" +
            pop();
    }

    //pop value from the stack, preceding string must finish by an addr to (repersenting the location to pop to)
    //stack pop: decrease pointer by one -> return removed value (decrease first since stack pointer is always the last index+1)
    //hack stack pop aim: rm value from main stack and store to segment
    //EG pop segment 1: pop top value from stack to segment 1
    public String pop(){
        return
            "@R13\n" + //load general purpose register (could also use R14, R15)
            "M=D\n" + //store addr of valueToReplace, allows us to reuse D
            "@SP\n" + //A=SP, M=*SP
            "AM=M-1\n" + //A=*SP-1, RAM[A]=RAM[A]-1 (*SP=*SP-1), (sets to A and M so A can be used for RAM[A] in the next line) deinc stack pointer
            "D=M\n" + //D=RAM[A] (RAM[*SP-1]), D=valuePopped
            "@R13\n" + //RAM[A]=addr of valueToReplace (which is the location to pop to)
            "A=M\n" + //A=RAM[A]
            "M=D\n"; //RAM[A]=*SP-1, valueToReplace=valuePopped
    }

    /**
     * Write the given text as a comment.
     * @param text the text to output.
     * @throws IOException 
     */
    public void writeComment(String text)
            throws IOException
    {
        output("// " + text);
    }
    
    /**
     * Close the output file.
     * @throws IOException 
     */
    public void close()
            throws IOException
    {
        try (writer) {
            String endOfProgram = "THATS_ALL_FOLKS";
            output("@" + endOfProgram);
            output(String.format("(%s)", endOfProgram));
            output("0;JMP");
        }
    }
    
    /**
     * Output the given string with an indent and
     * terminate the current line.
     * @param s The string to output.
     * @throws IOException 
     */
    private void output(String s)
            throws IOException
    {
        writer.write("    ");
        writer.write(s);
        writer.write('\n');
    }
}
