package com.example.witd2;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.witd2.ml.MobileFoodClassifier4Q;
import com.example.witd2.ml.MobileFoodClassifier5R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.tensorflow.lite.DataType;
import org.tensorflow.lite.support.common.ops.NormalizeOp;
import org.tensorflow.lite.support.image.ImageProcessor;
import org.tensorflow.lite.support.image.TensorImage;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import java.io.IOException;
import java.nio.ByteBuffer;

public class MainActivity extends AppCompatActivity {
    private ImageView imgView;
    private Button cameraButton, galleryButton, predictButton;
    private TextView outputBox;
    private TextView ingredientsBox;
    private Bitmap bitmap;

    String ingredientsJson = "{" +
            "\"apple_pie\": [\"apples\", \"sugar\", \"cinnamon\", \"pie crust\"]," +
            "\"baby_back_ribs\": [\"pork ribs\", \"barbecue sauce\", \"spices\"]," +
            "\"baklava\": [\"phyllo dough\", \"walnuts\", \"butter\", \"sugar\", \"honey\"]," +
            "\"beef_carpaccio\": [\"beef tenderloin\", \"olive oil\", \"lemon\", \"parmesan\", \"arugula\"]," +
            "\"beef_tartare\": [\"beef tenderloin\", \"mustard\", \"egg yolk\", \"capers\", \"onions\"]," +
            "\"beet_salad\": [\"beets\", \"goat cheese\", \"walnuts\", \"balsamic vinegar\", \"olive oil\"]," +
            "\"beignets\": [\"flour\", \"sugar\", \"yeast\", \"milk\", \"powdered sugar\"]," +
            "\"bibimbap\": [\"rice\", \"vegetables\", \"beef\", \"soy sauce\", \"sesame oil\"]," +
            "\"bread_pudding\": [\"bread\", \"milk\", \"eggs\", \"sugar\", \"vanilla\"]," +
            "\"breakfast_burrito\": [\"tortilla\", \"eggs\", \"bacon\", \"cheese\", \"salsa\"]," +
            "\"bruschetta\": [\"baguette\", \"tomatoes\", \"garlic\", \"basil\", \"olive oil\"]," +
            "\"caesar_salad\": [\"romaine lettuce\", \"croutons\", \"parmesan\", \"Caesar dressing\"]," +
            "\"cannoli\": [\"ricotta cheese\", \"flour\", \"sugar\", \"cinnamon\", \"chocolate\"]," +
            "\"caprese_salad\": [\"tomatoes\", \"mozzarella\", \"basil\", \"balsamic glaze\"]," +
            "\"carrot_cake\": [\"carrots\", \"flour\", \"sugar\", \"cream cheese\", \"walnuts\"]," +
            "\"ceviche\": [\"fish\", \"lime juice\", \"onions\", \"cilantro\", \"avocado\"]," +
            "\"cheesecake\": [\"cream cheese\", \"sugar\", \"eggs\", \"vanilla\", \"graham cracker crust\"]," +
            "\"cheese_plate\": [\"assorted cheeses\", \"crackers\", \"grapes\", \"nuts\"]," +
            "\"chicken_curry\": [\"chicken\", \"curry paste\", \"coconut milk\", \"vegetables\", \"rice\"]," +
            "\"chicken_quesadilla\": [\"chicken\", \"cheese\", \"tortilla\", \"salsa\", \"sour cream\"]," +
            "\"chicken_wings\": [\"chicken wings\", \"hot sauce\", \"butter\", \"celery\", \"ranch dressing\"]," +
            "\"chocolate_cake\": [\"flour\", \"sugar\", \"cocoa powder\", \"eggs\", \"butter\"]," +
            "\"chocolate_mousse\": [\"chocolate\", \"whipping cream\", \"sugar\", \"eggs\", \"vanilla\"]," +
            "\"churros\": [\"flour\", \"water\", \"salt\", \"cinnamon\", \"sugar\"]," +
            "\"clam_chowder\": [\"clams\", \"potatoes\", \"onions\", \"bacon\", \"cream\"]," +
            "\"club_sandwich\": [\"bread\", \"turkey\", \"bacon\", \"lettuce\", \"tomato\"]," +
            "\"crab_cakes\": [\"crab meat\", \"breadcrumbs\", \"mayonnaise\", \"mustard\", \"spices\"]," +
            "\"creme_brulee\": [\"heavy cream\", \"egg yolks\", \"sugar\", \"vanilla\", \"brown sugar\"]," +
            "\"croque_madame\": [\"bread\", \"ham\", \"cheese\", \"bechamel sauce\", \"fried egg\"]," +
            "\"cup_cakes\": [\"flour\", \"sugar\", \"eggs\", \"butter\", \"vanilla\"]," +
            "\"deviled_eggs\": [\"eggs\", \"mayonnaise\", \"mustard\", \"paprika\", \"chives\"]," +
            "\"donuts\": [\"flour\", \"sugar\", \"yeast\", \"milk\", \"chocolate glaze\"]," +
            "\"dumplings\": [\"flour\", \"water\", \"meat or vegetables\", \"soy sauce\", \"sesame oil\"]," +
            "\"edamame\": [\"edamame beans\", \"sea salt\"]," +
            "\"eggs_benedict\": [\"english muffin\", \"poached eggs\", \"ham\", \"hollandaise sauce\"]," +
            "\"escargots\": [\"snails\", \"garlic butter\", \"parsley\"]," +
            "\"falafel\": [\"chickpeas\", \"onion\", \"garlic\", \"coriander\", \"cumin\"]," +
            "\"filet_mignon\": [\"beef tenderloin\", \"salt\", \"pepper\", \"butter\", \"red wine sauce\"]," +
            "\"fish_and_chips\": [\"fish fillets\", \"potatoes\", \"flour\", \"baking soda\", \"malt vinegar\"]," +
            "\"foie_gras\": [\"goose liver\", \"salt\", \"pepper\", \"brioche\", \"fruit compote\"]," +
            "\"french_fries\": [\"potatoes\", \"vegetable oil\", \"salt\"]," +
            "\"french_onion_soup\": [\"onions\", \"beef broth\", \"baguette\", \"cheese\"]," +
            "\"french_toast\": [\"bread\", \"eggs\", \"milk\", \"cinnamon\", \"maple syrup\"]," +
            "\"fried_calamari\": [\"calamari\", \"flour\", \"cornmeal\", \"spices\", \"marinara sauce\"]," +
            "\"fried_rice\": [\"rice\", \"vegetables\", \"soy sauce\", \"eggs\", \"sesame oil\"]," +
            "\"frozen_yogurt\": [\"yogurt\", \"sugar\", \"vanilla\", \"fruit\"]," +
            "\"garlic_bread\": [\"baguette\", \"butter\", \"garlic\", \"parsley\"]," +
            "\"gnocchi\": [\"potatoes\", \"flour\", \"egg\", \"parmesan\", \"nutmeg\"]," +
            "\"greek_salad\": [\"cucumbers\", \"tomatoes\", \"feta cheese\", \"olives\", \"olive oil\"]," +
            "\"grilled_cheese_sandwich\": [\"bread\", \"cheese\", \"butter\"]," +
            "\"grilled_salmon\": [\"salmon fillet\", \"lemon\", \"olive oil\", \"dill\", \"garlic\"]," +
            "\"guacamole\": [\"avocado\", \"tomato\", \"onion\", \"lime\", \"cilantro\"]," +
            "\"gyoza\": [\"ground pork\", \"cabbage\", \"soy sauce\", \"ginger\", \"garlic\"]," +
            "\"hamburger\": [\"ground beef\", \"bun\", \"lettuce\", \"tomato\", \"cheese\"]," +
            "\"hot_and_sour_soup\": [\"tofu\", \"mushrooms\", \"bamboo shoots\", \"egg\", \"spices\"]," +
            "\"hot_dog\": [\"hot dog bun\", \"sausage\", \"mustard\", \"relish\"]," +
            "\"huevos_rancheros\": [\"eggs\", \"tortillas\", \"beans\", \"salsa\", \"cheese\"]," +
            "\"hummus\": [\"chickpeas\", \"tahini\", \"olive oil\", \"garlic\", \"lemon\"]," +
            "\"ice_cream\": [\"milk\", \"cream\", \"sugar\", \"flavorings\"]," +
            "\"lasagna\": [\"lasagna noodles\", \"ground beef\", \"tomato sauce\", \"ricotta cheese\", \"mozzarella cheese\"]," +
            "\"lobster_bisque\": [\"lobster\", \"broth\", \"cream\", \"sherry\", \"butter\"]," +
            "\"lobster_roll_sandwich\": [\"lobster meat\", \"mayonnaise\", \"celery\", \"lemon\", \"bun\"]," +
            "\"macaroni_and_cheese\": [\"macaroni\", \"cheese sauce\", \"butter\", \"milk\", \"flour\"]," +
            "\"macarons\": [\"almond flour\", \"powdered sugar\", \"egg whites\", \"buttercream\", \"flavorings\"]," +
            "\"miso_soup\": [\"miso paste\", \"tofu\", \"seaweed\", \"green onions\", \"dashi broth\"]," +
            "\"mussels\": [\"mussels\", \"white wine\", \"garlic\", \"shallots\", \"parsley\"]," +
            "\"nachos\": [\"tortilla chips\", \"cheese\", \"jalapenos\", \"salsa\", \"sour cream\"]," +
            "\"omelette\": [\"eggs\", \"fillings of your choice\", \"cheese\", \"butter\"]," +
            "\"onion_rings\": [\"onions\", \"flour\", \"breadcrumbs\", \"buttermilk\", \"spices\"]," +
            "\"oysters\": [\"oysters\", \"flour\", \"cornmeal\", \"spices\", \"remoulade sauce\"]," +
            "\"pad_thai\": [\"rice noodles\", \"shrimp\", \"tofu\", \"bean sprouts\", \"peanuts\"]," +
            "\"paella\": [\"rice\", \"chicken\", \"rabbit\", \"bell peppers\", \"saffron\"]," +
            "\"pancakes\": [\"flour\", \"milk\", \"eggs\", \"baking powder\", \"maple syrup\"]," +
            "\"panna_cotta\": [\"cream\", \"sugar\", \"gelatin\", \"vanilla\", \"fruit coulis\"]," +
            "\"peking_duck\": [\"duck\", \"hoisin sauce\", \"pancakes\", \"cucumber\", \"green onions\"]," +
            "\"pho\": [\"rice noodles\", \"beef broth\", \"beef slices\", \"bean sprouts\", \"lime\"]," +
            "\"pizza\": [\"dough\", \"tomato sauce\", \"cheese\", \"toppings of your choice\", \"olive oil\"]," +
            "\"pork_chop\": [\"pork chop\", \"salt\", \"pepper\", \"herbs\", \"butter\"]," +
            "\"poutine\": [\"fries\", \"cheese curds\", \"gravy\", \"salt\", \"spices\"]," +
            "\"prime_rib\": [\"beef rib roast\", \"salt\", \"pepper\", \"garlic\", \"rosemary\"]," +
            "\"pulled_pork_sandwich\": [\"pulled pork\", \"barbecue sauce\", \"coleslaw\", \"bun\"]," +
            "\"ramen\": [\"ramen noodles\", \"broth\", \"pork belly\", \"egg\", \"green onions\"]," +
            "\"ravioli\": [\"ravioli pasta\", \"ricotta cheese\", \"spinach\", \"marinara sauce\", \"parmesan\"]," +
            "\"red_velvet_cake\": [\"flour\", \"sugar\", \"cocoa powder\", \"buttermilk\", \"cream cheese frosting\"]," +
            "\"risotto\": [\"arborio rice\", \"broth\", \"white wine\", \"parmesan cheese\", \"butter\"]," +
            "\"samosa\": [\"potatoes\", \"peas\", \"spices\", \"flour\", \"chutney\"]," +
            "\"sashimi\": [\"fresh raw fish\", \"soy sauce\", \"wasabi\", \"pickled ginger\", \"seaweed\"]," +
            "\"scallops\": [\"scallops\", \"butter\", \"garlic\", \"lemon\", \"parsley\"]," +
            "\"seaweed_salad\": [\"seaweed\", \"soy sauce\", \"sesame oil\", \"rice vinegar\", \"sesame seeds\"]," +
            "\"shrimp_and_grits\": [\"shrimp\", \"grits\", \"bacon\", \"cheese\", \"green onions\"]," +
            "\"spaghetti_bolognese\": [\"spaghetti\", \"ground beef\", \"tomato sauce\", \"onions\", \"garlic\"]," +
            "\"spaghetti_carbonara\": [\"spaghetti\", \"bacon\", \"eggs\", \"parmesan cheese\", \"black pepper\"]," +
            "\"spring_rolls\": [\"rice paper\", \"shrimp\", \"vermicelli noodles\", \"lettuce\", \"hoisin-peanut sauce\"]," +
            "\"steak\": [\"steak\", \"salt\", \"pepper\", \"olive oil\", \"rosemary\"]," +
            "\"strawberry_shortcake\": [\"shortcake\", \"strawberries\", \"whipped cream\", \"sugar\"]," +
            "\"sushi\": [\"sushi rice\", \"fish or vegetables\", \"seaweed\", \"soy sauce\", \"wasabi\"]," +
            "\"tacos\": [\"tortillas\", \"meat or vegetables\", \"salsa\", \"cheese\", \"lime\"]," +
            "\"takoyaki\": [\"octopus\", \"batter\", \"bonito flakes\", \"takoyaki sauce\", \"mayonnaise\"]," +
            "\"tiramisu\": [\"ladyfingers\", \"espresso\", \"mascarpone cheese\", \"cocoa powder\", \"coffee liqueur\"]," +
            "\"tuna_tartare\": [\"fresh tuna\", \"soy sauce\", \"sesame oil\", \"avocado\", \"ginger\"]," +
            "\"waffles\": [\"waffle batter\", \"maple syrup\", \"butter\", \"fruit\", \"whipped cream\"]" +
            "}";

    JSONObject  jsonRootObject = new JSONObject(ingredientsJson);
    private final String[] CLASS_LABELS = {"apple_pie",
            "baby_back_ribs",
            "baklava",
            "beef_carpaccio",
            "beef_tartare",
            "beet_salad",
            "beignets",
            "bibimbap",
            "bread_pudding",
            "breakfast_burrito",
            "bruschetta",
            "caesar_salad",
            "cannoli",
            "caprese_salad",
            "carrot_cake",
            "ceviche",
            "cheesecake",
            "cheese_plate",
            "chicken_curry",
            "chicken_quesadilla",
            "chicken_wings",
            "chocolate_cake",
            "chocolate_mousse",
            "churros",
            "clam_chowder",
            "club_sandwich",
            "crab_cakes",
            "creme_brulee",
            "croque_madame",
            "cup_cakes",
            "deviled_eggs",
            "donuts",
            "dumplings",
            "edamame",
            "eggs_benedict",
            "escargots",
            "falafel",
            "filet_mignon",
            "fish_and_chips",
            "foie_gras",
            "french_fries",
            "french_onion_soup",
            "french_toast",
            "fried_calamari",
            "fried_rice",
            "frozen_yogurt",
            "garlic_bread",
            "gnocchi",
            "greek_salad",
            "grilled_cheese_sandwich",
            "grilled_salmon",
            "guacamole",
            "gyoza",
            "hamburger",
            "hot_and_sour_soup",
            "hot_dog",
            "huevos_rancheros",
            "hummus",
            "ice_cream",
            "lasagna",
            "lobster_bisque",
            "lobster_roll_sandwich",
            "macaroni_and_cheese",
            "macarons",
            "miso_soup",
            "mussels",
            "nachos",
            "omelette",
            "onion_rings",
            "oysters",
            "pad_thai",
            "paella",
            "pancakes",
            "panna_cotta",
            "peking_duck",
            "pho",
            "pizza",
            "pork_chop",
            "poutine",
            "prime_rib",
            "pulled_pork_sandwich",
            "ramen",
            "ravioli",
            "red_velvet_cake",
            "risotto",
            "samosa",
            "sashimi",
            "scallops",
            "seaweed_salad",
            "shrimp_and_grits",
            "spaghetti_bolognese",
            "spaghetti_carbonara",
            "spring_rolls",
            "steak",
            "strawberry_shortcake",
            "sushi",
            "tacos",
            "takoyaki",
            "tiramisu",
            "tuna_tartare",
            "waffles"};

    public MainActivity() throws JSONException {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getPermission();

        imgView = (ImageView) findViewById(R.id.ImageView);
        outputBox = (TextView) findViewById(R.id.textView);
        ingredientsBox = (TextView) findViewById(R.id.ingredientstextView);
        cameraButton = (Button) findViewById(R.id.CameraButton);
        galleryButton = (Button) findViewById(R.id.OpenGallery);
        predictButton = (Button) findViewById(R.id.Predict);



        galleryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, 100);

            }
        });

        cameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, 101);


            }
        });

        predictButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bitmap = Bitmap.createScaledBitmap(bitmap, 224, 224, true);

                try {
                    MobileFoodClassifier5R model = MobileFoodClassifier5R.newInstance(getApplicationContext());

                    ImageProcessor processor = new ImageProcessor.Builder()
                                    .add(new NormalizeOp(127.5f, 127.5f))
                                    .build();
                    TensorBuffer inputFeature0 = TensorBuffer.createFixedSize(new int[]{1, 224, 224, 3}, DataType.FLOAT32);
                    TensorImage tensorImage = new TensorImage(DataType.FLOAT32);
                    tensorImage.load(bitmap);
                    TensorImage normalTensorImage = processor.process(tensorImage);

                    ByteBuffer byteBuffer = normalTensorImage.getBuffer();
                    inputFeature0.loadBuffer(byteBuffer);

                    // Runs model inference and gets result.
                    MobileFoodClassifier5R.Outputs outputs = model.process(inputFeature0);
                    TensorBuffer outputFeature0 = outputs.getOutputFeature0AsTensorBuffer();

                    // Releases model resources if no longer used.
                    model.close();

                    float[] scores = outputFeature0.getFloatArray();
                    int maxIndex = 0;
                    for (int i = 1; i < scores.length; i++) {
                        if (scores[i] > scores[maxIndex]) {
                            maxIndex = i;
                        }
                    }
                    String predictedLabel = CLASS_LABELS[maxIndex];
                    StringBuilder builder = new StringBuilder();
                    builder.append(predictedLabel.toUpperCase().replace('_', ' '));
                    outputBox.setText(builder.toString());

                    String data = "Ingredients: \n";
                    try {
                        // Create the root JSONObject from the JSON string
                        JSONArray ingredients = jsonRootObject.getJSONArray(predictedLabel);

                        //Iterate the jsonArray and print the info of JSONObjects
                        for(int i=0; i < ingredients.length(); i++){

                            data += ingredients.getString(i) +" \n ";
                        }
                        ingredientsBox.setText(data);

                    } catch (JSONException e) {e.printStackTrace();}



                } catch (IOException e) {
                    StringBuilder builder = new StringBuilder();
                    builder.append("Take an Image or Select one from Gallery.");
                    outputBox.setText(builder.toString());

                }
            }
        });


    }
    void getPermission(){
        if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CAMERA}, 11);

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode ==11 ){
            if(grantResults.length > 0){
                if(grantResults[0] != PackageManager.PERMISSION_GRANTED){
                    this.getPermission();
                }
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode == 100)
        {
            imgView.setImageURI(data.getData());
            Uri uri = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            
        } else if (requestCode == 101) {
            bitmap = (Bitmap) data.getExtras().get("data");
            imgView.setImageBitmap(bitmap);

        }

        super.onActivityResult(requestCode, resultCode, data);

        
        
    }
}