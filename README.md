## Document preprocessing
This project hold various preprocessing steps to prepare raw documents like faktiva articles to be used as training data or weblyzard-portal data.

### Build and start cleaning process
Build jar by `mvn clean package`
Run processing by `java -jar ./target/preprocess-0.0.2-SNAPSHOT-jar-with-dependencies.jar {options}`

### Help
```
usage: preprocessor
 -charset <arg>    set charset of input files
 -csv              extracts text from weblyzard-portal csv download
 -csvfile <arg>    defines csv filename in combination with pick argument
 -doc              extracts doc* files to plain text
 -document         extracts content part of a json wl-document
 -e                extracts pdfs to plain text including healing steps
 -filename <arg>   filename to read csv from
 -h                adds header removal step to preprocessing
 -i <arg>          input directory or input file
 -o <arg>          output directory or output file
 -p <arg>          picks documents from directory with specified amount. A
                   csv with all picked filenames is created
 -rtf              extracts rtf files to plain text
 -s <arg>          start pdf extraction at pdf page number
 -subfolder        scans also subfolders for files
 -suffix <arg>     file ending to be scanned for in folders
 -zip              choose zip file from input directory
```

### Usage examples

`-createset -filename {}` creates a dataset from a csv template".

`-document -i {} -o {} -charset` extracts content part of a json wl-document (default charset UTF-8).

`-csv -i {} -o {}` extracts text from weblyzard-portal csv download

`-p -i {} -o {} -h -charset {}` preprocesses text files in folder + subfolders, splitts documents into parts and remove all headers.

`-pick {} -csvfile {} -i {} -o {} -charset {}` picks documents from directory with specified amount. A csv with all picked filenames is created (default charset UTF-8).

`-e -i {} -o {}[-doc | -rtf] -start {} -suffix {} -charset {}` extracts pdfs to plain text including healing steps.


### pdf cleaning steps
Pdf to text cleaner addresses common problems after a pdf is extracted to text
* Merges splitted words due to newlines. like Handels-\nabkommen'
* Merges newlines if no puncation at the end of the line
* Splits words with uppercase letters in a word
* Replaces commonly unrecognized signs like bulletpoints to '-' ...
* Removes pages keywords like 'Seite x von y' or 'Page x of y'
* Reduces reoccuring newlines to a maxiumum of 2
* Merges white-spaces words like 'W e d n e s d a y' to 'Wednesday'
