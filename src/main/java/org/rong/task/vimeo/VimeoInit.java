package org.rong.task.vimeo;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.rong.task.mybatis.MybatisSessionFactory;
import org.rong.task.util.DateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Table;

/**
 * Init vimeo task config
 * 
 * @author Rong
 * 
 */
public class VimeoInit {
	final static Logger logger = LoggerFactory.getLogger(VimeoInit.class);

	public static String[] tokenList;
	public static int threadCount;
	public static int page;
	public static int per_page;
	public static String accessKeyID;
	public static String secretAccessKey;
	public static String s3BucketName;
	public static String dynamoDBTableName;
	public static Table vimeoDynamoTable;
	public static String dataFile;

	public VimeoInit(String confFile) {
		Properties props = new Properties();
		try {
			InputStream in = new BufferedInputStream(new FileInputStream(
					confFile));
			props.load(in);
		} catch (IOException e) {
			logger.error("can't load config file: " + confFile);
		}
		try {
			String property = "";
			MybatisSessionFactory.init(props);

			property = "tokenList";
			if (props.getProperty(property) != null
					&& !props.getProperty(property).trim().equals("")) {
				VimeoInit.tokenList = props.getProperty(property).split(",");
				VimeoInit.threadCount = tokenList.length;
				logger.info("token list : " + props.getProperty(property));
				logger.info("thread count : " + VimeoInit.threadCount);
			} else {
				logger.error("missing token config");
				System.exit(-1);
			}

			property = "page";
			if (props.getProperty(property) != null
					&& !props.getProperty(property).trim().equals("")) {
				VimeoInit.page = Integer.parseInt(props.getProperty(property));
				logger.info("page : " + VimeoInit.page);
			} else {
				logger.error("missing page config");
				System.exit(-1);
			}

			property = "per_page";
			if (props.getProperty(property) != null
					&& !props.getProperty(property).trim().equals("")) {
				VimeoInit.per_page = Integer.parseInt(props
						.getProperty(property));
				logger.info("per_page : " + VimeoInit.per_page);
			} else {
				logger.error("missing per_page config");
				System.exit(-1);
			}

			property = "accessKeyID";
			if (props.getProperty(property) != null
					&& !props.getProperty(property).trim().equals("")) {
				VimeoInit.accessKeyID = props.getProperty(property);
				logger.info("accessKeyID : " + VimeoInit.accessKeyID);
			} else {
				logger.error("missing accessKeyID config");
				System.exit(-1);
			}

			property = "secretAccessKey";
			if (props.getProperty(property) != null
					&& !props.getProperty(property).trim().equals("")) {
				VimeoInit.secretAccessKey = props.getProperty(property);
				logger.info("secretAccessKey : " + VimeoInit.secretAccessKey);
			} else {
				logger.error("missing secretAccessKey config");
				System.exit(-1);
			}

			property = "s3BucketName";
			if (props.getProperty(property) != null
					&& !props.getProperty(property).trim().equals("")) {
				VimeoInit.s3BucketName = props.getProperty(property);
				logger.info("s3BucketName : " + VimeoInit.s3BucketName);
			} else {
				logger.error("missing s3BucketName config");
				System.exit(-1);
			}

			property = "dynamoDBTableName";
			if (props.getProperty(property) != null
					&& !props.getProperty(property).trim().equals("")) {
				VimeoInit.dynamoDBTableName = props.getProperty(property);
				logger.info("dynamoDBTableName : "
						+ VimeoInit.dynamoDBTableName);
			} else {
				logger.error("missing dynamoDBTableName config");
				System.exit(-1);
			}

			VimeoInit.vimeoDynamoTable = new DynamoDB(new AmazonDynamoDBClient(
					new BasicAWSCredentials(VimeoInit.accessKeyID,
							VimeoInit.secretAccessKey)))
					.getTable(VimeoInit.dynamoDBTableName);

			VimeoInit.dataFile = "Vimeo/Vimeo_"
					+ DateUtil.format(DateUtil.getNowHour(), "yyyyMMddHH")
					+ ".txt";
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
	}
}
