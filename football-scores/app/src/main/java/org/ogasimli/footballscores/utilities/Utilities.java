package org.ogasimli.footballscores.utilities;

import com.bumptech.glide.GenericRequestBuilder;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.model.StreamEncoder;
import com.bumptech.glide.load.resource.file.FileToStreamDecoder;
import com.caverock.androidsvg.SVG;
import com.ogasimli.footballscores.R;

import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.ogasimli.footballscores.svg.SvgDecoder;
import org.ogasimli.footballscores.svg.SvgDrawableTranscoder;
import org.ogasimli.footballscores.svg.SvgSoftwareLayerSetter;

import android.content.Context;
import android.graphics.drawable.PictureDrawable;
import android.net.Uri;
import android.util.Log;

import java.io.InputStream;

/**
 * Created by yehya khaled on 3/3/2015.
 */
public class Utilities {

    private static final String LOG_TAG = Utilities.class.getSimpleName();

    // This set of league codes is for the 2015/2016 season. In fall of 2016, they will need to
    // be updated. Feel free to use the codes
    public static final int BUNDESLIGA1 = 394;
    public static final int BUNDESLIGA2 = 395;
    private static final int BUNDESLIGA3 = 403;
    private static final int LIGUE1 = 396;
    private static final int LIGUE2 = 397;
    public static final int PREMIER_LEAGUE = 398;
    public static final int PRIMERA_DIVISION = 399;
    private static final int SEGUNDA_DIVISION = 400;
    public static final int SERIE_A = 401;
    private static final int PRIMERA_LIGA = 402;
    private static final int EREDIVISIE = 404;
    private static final int CHAMPIONS_LEAGUE = 405;

    public static String getLeague(Context context, int leagueId) {

        switch (leagueId) {
            case BUNDESLIGA1:
                return context.getString(R.string.bundesliga_1);

            case BUNDESLIGA2:
                return context.getString(R.string.bundesliga_2);

            case BUNDESLIGA3:
                return context.getString(R.string.bundesliga_3);

            case PREMIER_LEAGUE:
                return context.getString(R.string.premier_league);

            case SERIE_A :
                return context.getString(R.string.seria_a);

            case PRIMERA_DIVISION:
                return context.getString(R.string.primera_division);

            case SEGUNDA_DIVISION:
                return context.getString(R.string.segunda_division);

            case LIGUE1:
                return context.getString(R.string.ligue_1);

            case LIGUE2:
                return context.getString(R.string.ligue_2);

            case PRIMERA_LIGA:
                return context.getString(R.string.primera_liga);

            case EREDIVISIE:
                return context.getString(R.string.eredivisie);

            case CHAMPIONS_LEAGUE:
                return context.getString(R.string.champions_league);

            default:
                return "";
        }
    }

    public static String getScores(int homeTeamGoals,int awayTeamGoals) {
        if(homeTeamGoals < 0 || awayTeamGoals < 0)
            return " - ";
        else
            return String.valueOf(homeTeamGoals) + " - " + String.valueOf(awayTeamGoals);
    }

    public static String getDateMillisForQueryFormat(long dateMillis) {
        LocalDate localDate = new LocalDate(dateMillis);
        DateTimeFormatter fmt = DateTimeFormat.forPattern("yyyy-MM-dd");

        String dateForQueryFormat = fmt.print(localDate);
        Log.d(LOG_TAG, "Date for fragment: " + dateForQueryFormat);

        return dateForQueryFormat;
    }

    public static LocalDate getLocalDateForItem(int position) {
        LocalDate localDate = new LocalDate();
        localDate = localDate.plusDays(position - 2);

        Log.d(LOG_TAG, "Position: " + position + " / " + localDate.toString());

        return localDate;
    }

    public static String translateDayOfWeek(Context context, LocalDate localDate){
        int dayOfWeek = localDate.getDayOfWeek();
        String[] daysOfWeekText = context.getResources().getStringArray(R.array.days_of_week);

        switch (dayOfWeek) {
            case 1:
                return daysOfWeekText[0];

            case 2:
                return daysOfWeekText[1];

            case 3:
                return daysOfWeekText[2];

            case 4:
                return daysOfWeekText[3];

            case 5:
                return daysOfWeekText[4];

            case 6:
                return daysOfWeekText[5];

            case 7:
                return daysOfWeekText[6];

            default:
                return "";
        }
    }

    public static GenericRequestBuilder<Uri, InputStream, SVG,
            PictureDrawable> getRequestBuilder(Context context) {
        return Glide.with(context)
                .using(Glide.buildStreamModelLoader(Uri.class, context), InputStream.class)
                .from(Uri.class)
                .as(SVG.class)
                .transcode(new SvgDrawableTranscoder(), PictureDrawable.class)
                .sourceEncoder(new StreamEncoder())
                .cacheDecoder(new FileToStreamDecoder<>(new SvgDecoder()))
                .decoder(new SvgDecoder())
                .listener(new SvgSoftwareLayerSetter<Uri>());
    }
}
