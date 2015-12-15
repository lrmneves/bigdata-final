#Big Data Analytics Final

###Run Hadoop Code

For you to run the Hadoop code, you can either compile the code into a jar using maven (`mvn package`), or download the jar file from this link: https://www.dropbox.com/s/h4nw5qixdspbdxq/BigDataFinal-0.0.1-SNAPSHOT.jar?dl=0

To use the same samples I did, you can download the tar file from https://www.dropbox.com/s/445qgn16lu235uj/samples.tar?dl=0
 
You should, on the same directory as the tar file, create a folder called input with `mkdir input` and extract samples.tar inside the input folder.
The command to run it is then: 
`export HADOOP_OPTS="-Xmx4096m";nohup java -cp BigDataFinal-0.0.1-SNAPSHOT.jar hadoop.TFIDFDriver &`.

The `HADOOP_OPTS` flag works to make Hadoop allocating more heap space before running, so the job can handle more data. `nohup command &` makes the command run on the background. If this is not the expecte behavior, the user can remove those words. Also, for running it on the cluster, it was not possible to use the whole data. This happened because the cluster had a limit for the meta-data split size and could not handle the ammount of data.

It is important to say that the temporary results will be stored into a results folder. If you wish to run the experiments again, you need to delete the previous results inside the results folder.


###Run preprocessing

To run preprocessing, have the script to be on the same folder as the folders with the raw text files. If enabled, joblib will use 8 threads to preprocess data in parallel and store the results on a folder called processedDocuments. This resulting folder should be copied to the input directory as mentioned on the Hadoop instruction for the TF-IDF calculation part.

To run it on the cluster, I had to make some changes and it won't run with joblib. Please find the code at ~/lneves/preprocess.py

###Notebook

To run the IPython Notebook, please run the command `ipython notebook` on the main folder of this project. The files have interactive visualizations, so I was not able to create a static version of the notebook.