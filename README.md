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
Run processing by `java -jar ./target/preprocess-0.0.2-SNAPSHOT-jar-with-dependencies.jar -inputDir {input directory with .txt files} -outputDir {output diretory}`

Note: options -inputDir and -outputDir are mandatory

### Start extraction process
`java -jar ./target/preprocess-0.0.2-SNAPSHOT-jar-with-dependencies.jar -e -start 3 -inputDir {input directory with .pdf files} -outputDir {output directory}`

Note:   
* options `-inputDir` and `-outputDir` are mandatory
* option `-e` -> extraction from pdf to text
* with option `-e` set you can also define `-start {int}` which describes the pdf page to start extraction at

### Start Weblyzard CSV exporter
Exports Weblyzard Portal CSV format to textfile per row
use `-csv` in combination with `-inputDir` and `-outputDir.`
`java -jar ./target/preprocess-0.0.2-SNAPSHOT-jar-with-dependencies.jar -csv -inputDir {input path to csv file} -outputDir {output directory}`

### Start document preparation (spliting and header removal)
Splits documents according to `(?m)^Dokument \\w+$";` and removes header with `(?m)^.*?\\b(copyright|Copyright|(c))\\b.*$`.
use `-prepare` in combination with `-inputDir` and `-outputDir.`

`java -jar ./target/preprocess-0.0.2-SNAPSHOT-jar-with-dependencies.jar -prepare -header -inputDir {input path to folder containing txt files} -outputDir {output directory}`.

If inputDirectory holds zip files to extract first, use additional option `-zip` to read zip files directly and pass them to preparation processing.

`java -jar ./target/preprocess-0.0.2-SNAPSHOT-jar-with-dependencies.jar -prepare -zip -header -inputDir {input path to folder containing txt files} -outputDir {output directory}`.

### Pick specified amount of files in randomized manner, create a csv file with the randomized filenames and write files and csv file to outputFolder
use `-pick {amount}` `-csvfile {only filename}` `-inputDir` `-outputDir`

### Extracts files with .docx ending to plain text
use `-e` `-doc` `-inputDir` `-ouputDir`

### Extract content part to simple text of a persisted WL-Document json
use `-document` `-inputDir` `-ouputDir`
