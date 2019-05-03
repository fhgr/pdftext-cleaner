## PDF to Text Cleaner
Pdf to text cleaner addresses common problems after a pdf is extracted to text
* Merges splitted words due to newlines. like Handels-\nabkommen'
* Merges newlines if no puncation at the end of the line
* Splits words with uppercase letters in a word
* Replaces commonly unrecognized signs like bulletpoints to '-' ...
* Removes pages keywords like 'Seite x von y' or 'Page x of y'
* Reduces reoccuring newlines to a maxiumum of 2
* Merges white-spaces words like 'W e d n e s d a y' to 'Wednesday'

## Weblyzard Portal CSV to Text file extractor
* Reads Weblyzard Portal export csv and extracts rows to text-file (only text column)
* Creates murmur3-128bit hash from text for filename to keep them distinct

Note -inputDir is in this case a csv file

### Build and start cleaning process
Build jar by `mvn clean package`
Run processing by `java -jar ./target/pdfhealer-0.0.1-SNAPSHOT-jar-with-dependencies.jar -inputDir {input directory with .txt files} -outputDir {output diretory}`

Note: options -inputDir and -outputDir are mandatory

### Start extraction process
`java -jar ./target/pdfhealer-0.0.1-SNAPSHOT-jar-with-dependencies.jar -e -start 3 -inputDir {input directory with .pdf files} -outputDir {output directory}`

Note:   
* options `-inputDir` and `-outputDir` are mandatory
* option `-e` -> extraction from pdf to text
* with option `-e` set you can also define `-start {int}` which describes the pdf page to start extraction at

### Start Weblyzard CSV exporter
Exports Weblyzard Portal CSV format to textfile per row
use `-csv` in combination with `-inputDir` and `-outputDir.`
`java -jar ./target/pdfhealer-0.0.1-SNAPSHOT-jar-with-dependencies.jar -csv -inputDir {input path to csv file} -outputDir {output directory}`
