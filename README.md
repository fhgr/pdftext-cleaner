# PDF to Text Cleaner
Pdf to text cleaner addresses common problems after a pdf is extracted to text
* Merges splitted words due to newlines. like Handels-\nabkommen'
* Merges newlines if no puncation at the end of the line
* Splits words with uppercase letters in a word
* Replaces commonly unrecognized signs like bulletpoints to '-' ...
* Removes pages keywords like 'Seite x von y' or 'Page x of y'
* Reduces reoccuring newlines to a maxiumum of 2
* Merges white-spaces words like 'W e d n e s d a y' to 'Wednesday'

## Build and start cleaning process
Build jar by `mvn clean package`
Run processing by `java -jar ./target/pdfhealer-0.0.1-SNAPSHOT-jar-with-dependencies.jar -i {input directory with .txt files} -o {output diretory}`

Note: options -i and -o are mandatory
