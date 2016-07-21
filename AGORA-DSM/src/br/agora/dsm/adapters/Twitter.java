package br.agora.dsm.adapters;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpException;
import org.json.simple.JSONObject;

import br.agora.dsm.sensormanagement.PublishObservationV2;
import twitter4j.FilterQuery;
import twitter4j.StallWarning;
import twitter4j.Status;
import twitter4j.StatusDeletionNotice;
import twitter4j.StatusListener;
import twitter4j.TwitterException;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;
import twitter4j.conf.ConfigurationBuilder;

public class Twitter {

	public static void receiveTweets() throws TwitterException {

		ConfigurationBuilder cb = new ConfigurationBuilder();
		cb.setDebugEnabled(true);
		cb.setOAuthConsumerKey("DNODL18Ja1aBYNfQBFUq0YZSZ");
		cb.setOAuthConsumerSecret("KyH5nDIOzxcdbFt5DfpLObLczsLZLsBAEwiocGgZACJvMdjtS0");
		cb.setOAuthAccessToken("1240286418-rjcfB3akfqMAtxdGnuqXXx5FEh3cWUJIRmI0d80");
		cb.setOAuthAccessTokenSecret("C2YqLDwzJwLjIZV4lF8N1euRrYtZ5XZl1FbFfzGafEwnY");

		TwitterStream twitterStream = new TwitterStreamFactory(cb.build()).getInstance();

		StatusListener listener = new StatusListener() {

			public void onStatus(Status status) {

				if (status.getGeoLocation() != null) {

					try {

						String statusText = status.getText().replaceAll("'", " ");

						JSONObject jsonAdapter = new JSONObject();

						jsonAdapter.put("sensor_id", status.getUser().getId());

						jsonAdapter.put("sensor_name", status.getUser().getScreenName());

						/*************************
						 * UTC Format Tweets
						 ***********************/
						int year = status.getCreatedAt().getYear() + 1900;
						String month, day, hour, minutes, seconds;

						// check day parameter
						if (status.getCreatedAt().getDate() < 10)
							day = "0" + status.getCreatedAt().getDate();
						else
							day = Integer.toString(status.getCreatedAt().getDate());

						// check month parameter
						if (status.getCreatedAt().getMonth() + 1 < 10)
							month = "0" + (status.getCreatedAt().getMonth() + 1);
						else
							month = Integer.toString(status.getCreatedAt().getMonth() + 1);

						// check hour parameter
						if (status.getCreatedAt().getHours() < 10)
							hour = "0" + status.getCreatedAt().getHours();
						else
							hour = Integer.toString(status.getCreatedAt().getHours());

						// check minutes parameter
						if (status.getCreatedAt().getMinutes() < 10)
							minutes = "0" + status.getCreatedAt().getMinutes();
						else
							minutes = Integer.toString(status.getCreatedAt().getMinutes());

						// check seconds parameter
						if (status.getCreatedAt().getSeconds() < 10)
							seconds = "0" + status.getCreatedAt().getSeconds();
						else
							seconds = Integer.toString(status.getCreatedAt().getSeconds());

						jsonAdapter.put("timestamp",
								year + "-" + month + "-" + day + "T" + hour + ":" + minutes + ":" + seconds + "+00:00");

						jsonAdapter.put("value", statusText.toString());

						jsonAdapter.put("type", "text");
						jsonAdapter.put("unit", "post");
						jsonAdapter.put("agency", "Twitter");

						jsonAdapter.put("sensor_place_name", status.getPlace().getFullName());

						JSONObject coordinates = new JSONObject();

						coordinates.put("longitude", status.getGeoLocation().getLongitude());

						coordinates.put("latitude", status.getGeoLocation().getLatitude());

						jsonAdapter.put("coordinates", coordinates);

						PublishObservationV2.send(jsonAdapter);

					} catch (HttpException e) {
						// TODO Auto-generated catch block
						System.out.println("Twitter API - Publish Observation - " + e);
					}

				}

			}

			public void onDeletionNotice(StatusDeletionNotice statusDeletionNotice) {
				System.out.println("Got a status deletion notice id:" + statusDeletionNotice.getStatusId());
			}

			public void onTrackLimitationNotice(int numberOfLimitedStatuses) {
				System.out.println("Got track limitation notice:" + numberOfLimitedStatuses);
			}

			public void onScrubGeo(long userId, long upToStatusId) {
				System.out.println("Got scrub_geo event userId:" + userId + " upToStatusId:" + upToStatusId);
			}

			public void onException(Exception ex) {
				ex.printStackTrace();
			}

			@Override
			public void onStallWarning(StallWarning arg0) {
				// TODO Auto-generated method stub

			}
		};

		FilterQuery fq = new FilterQuery();

		// calculating bounding box
		List<double[]> rowList = new ArrayList<double[]>();

		double xmin = -53.183, ymin = -25.127, xmax = -44.0938, ymax = -19.6895;

		double xmin_i, ymin_i, xmax_i, ymax_i;

		for (int i = 0; i < 5; i++)
			for (int j = 0; j < 5; j++) {
				xmin_i = xmin + (i * (xmax - xmin) / 5);
				ymin_i = ymin + (j * (ymax - ymin) / 5);
				xmax_i = xmin + ((i + 1) * (xmax - xmin) / 5);
				ymax_i = ymin + ((j + 1) * (ymax - ymin) / 5);
				rowList.add(new double[] { xmin_i, ymin_i });
				rowList.add(new double[] { xmax_i, ymax_i });
			}

		double loc[][] = new double[50][2];

		for (int i = 0; i < 50; i++) {
			loc[i][0] = rowList.get(i)[0];
			loc[i][1] = rowList.get(i)[1];
		}

		twitterStream.addListener(listener);

		fq.locations(loc);

		twitterStream.filter(fq);

	}

}