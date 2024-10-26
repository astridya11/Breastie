package fyp.yuki.vitrox2;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ChatbotActivity extends AppCompatActivity {
    private Context context;
    RecyclerView recyclerView;
    EditText messageEditText;
    TextView default_response;
    ImageButton sendButton;
    List<chatbotMessage> messageList;
    chatbotMessageAdapter messageAdapter;
    ListView questions;
    LinearLayout default_chat_view;
    RelativeLayout center_layout;
    ArrayAdapter<String> adapter;
    public static final MediaType JSON = MediaType.get("application/json");
    OkHttpClient client = new OkHttpClient();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatbot);
        messageList = new ArrayList<>();

        recyclerView = findViewById(R.id.recycler_view);
        messageEditText = findViewById(R.id.message_edit_text);
        sendButton = findViewById(R.id.send_btn);

        //setup recycler view
        messageAdapter = new chatbotMessageAdapter(messageList);
        recyclerView.setAdapter(messageAdapter);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setStackFromEnd(true);
        recyclerView.setLayoutManager(llm);

        center_layout = findViewById(R.id.center_layout);
        default_response = findViewById(R.id.default_response);
        questions = findViewById(R.id.question);
        default_chat_view = findViewById(R.id.default_chat_view);

        ArrayList<String> questions_array = new ArrayList<>();
        questions_array.add("Who is likely to get breast cancer?");
        questions_array.add("What are the types of breast cancer?");
        questions_array.add("What are the symptoms of breast cancer?");
        questions_array.add("What are the tests available to confirm the presence of breast cancer?");
        questions_array.add("What is Breast Self Examination (BSE)?");
        questions_array.add("What can I do to reduce the risk for breast cancer?");
        questions_array.add("What are the treatment options for breast cancer?");
        questions_array.add("Will men have breast cancer?");

        adapter = new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,questions_array);
        adapter.setDropDownViewResource(android.R.layout.select_dialog_singlechoice);
        questions.setAdapter(adapter);

        questions.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selected_question = (String) parent.getItemAtPosition(position);
                addToChat(selected_question, chatbotMessage.SENT_BY_ME);
                callAPI(selected_question);
            }
        });

        sendButton.setOnClickListener((v)->{
            String question = messageEditText.getText().toString().trim();
            addToChat(question,chatbotMessage.SENT_BY_ME);
            messageEditText.getText().clear();
            callAPI(question);
        });

        context = this;
    }

    void addToChat(String message, String sentBy){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                recyclerView.setVisibility(View.VISIBLE);
                messageList.add(new chatbotMessage(message,sentBy));
                messageAdapter.notifyDataSetChanged();
                recyclerView.smoothScrollToPosition(messageAdapter.getItemCount());
                default_chat_view.setVisibility(View.GONE);
                questions.setVisibility(View.GONE);
            }

            });

    }

    void addResponse (String response){
        addToChat(response, chatbotMessage.SENT_BY_BOT);
    }

    void handleIntent(Context context){
        String responseText = "Reminder: It's always best to consult with a healthcare provider for professional advice. \nWe have more exciting features! Click to explore our information page, AI meal plan, AR self examination and community group.";
        SpannableString spannableString = new SpannableString(responseText);

//        TO_DO- Make sure this is same with yuki and minmin's code
//        Define activity class
        Class<?> infoPageActivity = MainActivity.class;
//        Class<?> infoPageActivity = InfoPageActivity.class;
//        Class<?> aiMealPlanActivity = AIMealPlanActivity.class;
//        Class<?> arSelfExaminationActivity = ARSelfExaminationActivity.class;
//        Class<?> communityGroupActivity = CommunityGroupActivity.class;

        // Define the clickable text ranges
        String infoPage = "information page";
        String aiMealPlan = "AI meal plan";
        String arSelfExamination = "AR self examination";
        String communityGroup = "community group";

        setClickableSpan(spannableString, infoPage, context, infoPageActivity);
//        setClickableSpan(spannableString, aiMealPlan, context, aiMealPlanActivity);
//        setClickableSpan(spannableString, arSelfExamination, context, arSelfExaminationActivity);
//        setClickableSpan(spannableString, communityGroup, context, communityGroupActivity);

        center_layout.setVisibility(View.VISIBLE);
        default_response.setText(spannableString);
        default_response.setMovementMethod(LinkMovementMethod.getInstance());



//        TextView textView = new TextView(this);
//        textView.setLayoutParams(new ViewGroup.LayoutParams(
//                ViewGroup.LayoutParams.WRAP_CONTENT,
//                ViewGroup.LayoutParams.WRAP_CONTENT));
//
//        // Set the SpannableString to the TextView
//        textView.setText(spannableString);
//
//        // Enable clickable spans
//        textView.setMovementMethod(LinkMovementMethod.getInstance());
//        // Set text color and text size
//        textView.setTextColor(getResources().getColor(R.color.black));  // Replace with your color resource
//        textView.setTextSize(18);  // 18sp
//
//        // Add the TextView to your layout
//        @NonNull ViewGroup layout = findViewById(R.id.chatbot_view);  // Replace with your layout ID
//        layout.addView(textView);

    }

    private void setClickableSpan(SpannableString spannableString, String clickableText, Context context, Class<?> targetActivity){
        int startIndex = spannableString.toString().indexOf(clickableText);
        if(startIndex != -1){
            int endIndex = startIndex + clickableText.length();
            ClickableSpan clickableSpan = new ClickableSpan() {
                @Override
                public void onClick(@NonNull View widget) {
                    Intent intent = new Intent(context,targetActivity);
                    context.startActivity(intent);
                }
            };
            spannableString.setSpan(clickableSpan,startIndex,endIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            spannableString.setSpan(new ForegroundColorSpan(Color.BLUE), startIndex,endIndex,Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
    }

    void callAPI(String question){

        JSONObject jsonBody = new JSONObject();
        try {
            //jsonBody.put("model","gpt-3.5-turbo-1106");
            //jsonBody.put("name","asst_1PN1ZR1zyMAOOfiMkWoA1opn");
            jsonBody.put("userId", "breast_cancer_chatbot");
            jsonBody.put("messageId", "breast_cancer_message");
            jsonBody.put("conversationId", "breast_cancer_conversation");
            jsonBody.put("type", "text");
            jsonBody.put("text", question);
            jsonBody.put("payload", new JSONObject().put("role", "user"));
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        RequestBody body = RequestBody.create(jsonBody.toString(),JSON);
        Request request = new Request.Builder()
                .url("https://webhook.botpress.cloud/56a92c43-0ca4-4f5e-bbe5-10d3323f785d")
                .header("Authorization","Bearer bp_pat_dzW7dZT7HwRHJiI6XE04E0WcMCS5Ncljo12L")
                .post(body)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                addResponse("Failed to load response due to " + e.getMessage());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if(response.isSuccessful()){
                    //JSONObject jsonObject = null;
                    try {
                        String responseBody = response.body().string();
                        Log.d("Response", responseBody);
                        JSONObject jsonObject = new JSONObject(responseBody);
                       //jsonObject = new JSONObject(response.body().string());
                        //JSONArray jsonArray = jsonObject.getJSONArray("payload");
                       // JSONObject payload = jsonObject.getJSONObject("message").getJSONObject("payload");
                        //String result = payload.getString("text");
                       // addResponse(jsonObject.toString());
                        //Log.d("Response", response.body().string());
//                        runOnUiThread(()->{
//                            handleIntent(context);
//                        });

                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }

                }else{
                    addResponse("Failed to load response due to" + response.body().string());
//                    runOnUiThread(() -> {
//                        handleIntent(context);
//                            }
//                    );
                }

            }
        });
    }

}
