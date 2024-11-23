package com.example.studyguider.view;


import android.app.AlertDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;


import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.lifecycle.ViewModelProvider;


import com.example.studyguider.R;
import com.example.studyguider.models.Planner;
import com.example.studyguider.viewmodels.PlannerViewModel;
import com.example.studyguider.viewmodels.HeaderViewModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class PlannerActivity extends AppCompatActivity {

    private Planner currentEditingEvent;
    private GridLayout gridLayoutCalendar;
    private ScrollView informationScrollView;
    private LinearLayout savedEventsLayout;


    private EditText editTextEventName;
    private EditText editTextAdditionalInfo;

    private Button saveButton;
    private Button colorPickerButton;
    private Button buttonNextMonth;
    private Button buttonPreviousMonth;

    private TextView selectedDayTextView;
    private TextView textViewEventTime;
    private TextView monthTextView;


    private PlannerViewModel plannerViewModel;
    private Map<String, Integer> dayColors = new HashMap<>();


    private int currentMonth;
    private int currentYear;


    FirebaseAuth auth = FirebaseAuth.getInstance();
    private HeaderViewModel headerViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_planner);


        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        monthTextView = findViewById(R.id.textViewMonth);
        buttonPreviousMonth = findViewById(R.id.buttonPreviousMonth);
        buttonPreviousMonth.setText("<");


        buttonNextMonth = findViewById(R.id.buttonNextMonth);
        buttonNextMonth.setText(">");


        plannerViewModel = new ViewModelProvider(this).get(PlannerViewModel.class);

        headerViewModel = new ViewModelProvider(this).get(HeaderViewModel.class);

        View headerView = findViewById(R.id.header);
        HeaderActivity headerActivity = new HeaderActivity(headerView, headerViewModel, this);

        // Configurações do cabeçalho
        FirebaseUser  currentUser1 = FirebaseAuth.getInstance().getCurrentUser ();
        if (currentUser1 != null) {
            headerViewModel.fetchUsername(currentUser1);
        }

        setupObservers(); // Certifique-se de que isso está após o carregamento dos eventos


        gridLayoutCalendar = findViewById(R.id.gridLayoutCalendar);
        informationScrollView = findViewById(R.id.informationScrollView);
        savedEventsLayout = findViewById(R.id.savedEventsLayout);


        editTextEventName = findViewById(R.id.editTextEventName);
        textViewEventTime = findViewById(R.id.textViewEventTime);
        editTextAdditionalInfo = findViewById(R.id.editTextAdditionalInfo);
        saveButton = findViewById(R.id.saveButton);
        colorPickerButton = findViewById(R.id.colorPickerButton);


        Calendar calendar = Calendar.getInstance();
        currentMonth = calendar.get(Calendar.MONTH);
        currentYear = calendar.get(Calendar.YEAR);


        setupCalendar();
        setupObservers();


        colorPickerButton.setOnClickListener(v -> showColorPickerDialog());


        saveButton.setOnClickListener(v -> {
            try {
                if (selectedDayTextView == null) {
                    Toast.makeText(this, "Selecione um dia primeiro!", Toast.LENGTH_SHORT).show();
                } else {
                    saveEvent();
                }
            } catch (Exception e) {
                Log.e("PlannerActivity", "Error saving event: ", e);
                Toast.makeText(this, "Ocorreu um erro ao salvar o evento. Tente novamente.", Toast.LENGTH_SHORT).show();
            }
        });


        textViewEventTime.setOnClickListener(v -> showTimePickerDialog());


        buttonPreviousMonth.setOnClickListener(v -> {
            currentMonth--;
            if (currentMonth < 0) {
                currentMonth = 11;
                currentYear--;
            }
            setupCalendar();
            updateSavedEvents(plannerViewModel.getEvents().getValue());
        });


        buttonNextMonth.setOnClickListener(v -> {
            currentMonth++;
            if (currentMonth > 11) {
                currentMonth = 0;
                currentYear++;
            }
            setupCalendar();
            updateSavedEvents(plannerViewModel.getEvents().getValue());
        });

        setupCalendar();
        setupObservers();

        // Carregar eventos do Firestore
        FirebaseUser  currentUser  = auth.getCurrentUser ();
        if (currentUser  != null) {
            String userId = currentUser.getUid();
            plannerViewModel.loadEventsByUserId(userId); // Carrega eventos do usuário
        }

        setupObservers(); // Certifique-se de que isso está após o carregamento dos eventos
    }

    private void setupCalendar() {
        gridLayoutCalendar.removeAllViews();


        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.MONTH, currentMonth);
        calendar.set(Calendar.YEAR, currentYear);


        String monthName = calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, getResources().getConfiguration().locale);
        TextView monthTextView = findViewById(R.id.textViewMonth);
        monthTextView.setText(String.format("%s %d", monthName, currentYear));


        calendar.set(Calendar.DAY_OF_MONTH, 1);
        int daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);


        for (int day = 1; day <= daysInMonth; day++) {
            TextView dayTextView = new TextView(this);
            dayTextView.setText(String.valueOf(day));
            dayTextView.setGravity(Gravity.CENTER);
            dayTextView.setBackgroundColor(dayColors.getOrDefault(getDayKey(day), Color.WHITE));
            dayTextView.setPadding(16, 16, 16, 16);


            int finalDay = day;
            dayTextView.setOnClickListener(v -> handleDayClick(dayTextView, finalDay));


            GridLayout.LayoutParams param = new GridLayout.LayoutParams();
            param.height = GridLayout.LayoutParams.WRAP_CONTENT;
            param.width = 0;
            param.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
            dayTextView.setLayoutParams(param);


            gridLayoutCalendar.addView(dayTextView);
        }
    }


    private void handleDayClick(TextView dayTextView, int day) {
        String dayKey = getDayKey(day);
        if (dayColors.getOrDefault(dayKey, Color.WHITE) != Color.WHITE) {
            Toast.makeText(this, "Este dia já tem um evento", Toast.LENGTH_SHORT).show();
            return;
        }

        selectedDayTextView = dayTextView;
        dayTextView.setBackgroundColor(plannerViewModel.getSelectedColor().getValue());
        dayColors.put(dayKey, plannerViewModel.getSelectedColor().getValue());
        plannerViewModel.selectDay(String.valueOf(day));
        informationScrollView.setVisibility(View.VISIBLE);
        gridLayoutCalendar.setVisibility(View.GONE);
        savedEventsLayout.setVisibility(View.GONE);

        // Esconde os botões e o TextView do mês
        buttonPreviousMonth.setVisibility(View.GONE);
        buttonNextMonth.setVisibility(View.GONE);
        monthTextView.setVisibility(View.GONE);
    }

    private void showColorPickerDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Escolha uma cor");


        String[] colors = {"Roxo", "Rosa", "Amarelo", "Azul"};
        int[] colorValues = {Color.parseColor("#CB9BDE"), Color.parseColor("#FFCBDB"), Color.parseColor("#FFFFCC"), Color.parseColor("#87CEEB")};


        builder.setItems(colors, (dialog, which) -> {
            plannerViewModel.setColor(colorValues[which]);
        });


        AlertDialog dialog = builder.create();
        dialog.show();
    }


    private void showTimePickerDialog() {
        int hour = 12;
        int minute = 0;


        new android.app.TimePickerDialog(this, (view, selectedHour, selectedMinute) -> {
            String time = String.format("%02d:%02d", selectedHour, selectedMinute);
            textViewEventTime.setText(time);
        }, hour, minute, true).show();
    }


    private void saveEvent() {
        try {
            // Obtém os valores do ViewModel e os campos do formulário
            String day = plannerViewModel.getSelectedDay().getValue();
            String eventName = editTextEventName.getText().toString().trim();
            String eventTime = textViewEventTime.getText().toString().trim();
            String additionalInfo = editTextAdditionalInfo.getText().toString().trim();

            Log.d("PlannerActivity", "Saving event: " + eventName + ", Time: " + eventTime + ", Additional Info: " + additionalInfo);

            // Valida se o nome do evento foi preenchido
            if (eventName.isEmpty()) {
                Toast.makeText(this, "Preencha todos os campos obrigatórios!", Toast.LENGTH_SHORT).show();
                return;
            }

            // Verifica se o horário foi selecionado
            if (eventTime.equals("Clique Aqui")) {
                Toast.makeText(this, "Selecione um horário!", Toast.LENGTH_SHORT).show();
                return;
            }

            // Verifica se uma cor foi selecionada
            Integer selectedColor = plannerViewModel.getSelectedColor().getValue();
            if (selectedColor == null || selectedColor == Color.WHITE) {
                Toast.makeText(this, "Selecione uma cor para o evento!", Toast.LENGTH_SHORT).show();
                return;
            }

            // Se estivermos editando um evento existente
            if (currentEditingEvent != null) {
                // Atualiza o evento existente
                currentEditingEvent.setEventName(eventName);
                currentEditingEvent.setEventTime(eventTime);
                currentEditingEvent.setAdditionalInfo(additionalInfo);
                currentEditingEvent.setColor(selectedColor);
                Log.d("PlannerActivity", "Updating event: " + currentEditingEvent.toString());
                plannerViewModel.updateEvent(currentEditingEvent);
            } else {
                // Adiciona um novo evento
                String dayKey = getDayKey(Integer.parseInt(day));
                plannerViewModel.removeEventByDay(dayKey);
                FirebaseUser  user = auth.getCurrentUser ();
                String userId = user.getUid();
                plannerViewModel.addEvent(userId, eventName, eventTime, additionalInfo, selectedColor, dayKey);
            }

            // Atualiza a cor do dia no calendário
            if (selectedDayTextView != null) {
                dayColors.put(getDayKey(Integer.parseInt(day)), selectedColor);
                selectedDayTextView.setBackgroundColor(selectedColor);
            }

            // Limpa os campos do formulário
            resetForm();

            // Atualiza a interface
            updateInterfaceAfterSave();
        } catch (Exception e) {
            Log.e("PlannerActivity", "Error in saveEvent: ", e);
            Toast.makeText(this, "Ocorreu um erro ao salvar o evento. Tente novamente.", Toast.LENGTH_SHORT).show();
        }
    }

    private void resetForm() {
        selectedDayTextView = null;
        editTextEventName.setText("");
        textViewEventTime.setText("Selecione o horário");
        editTextAdditionalInfo.setText("");
    }

    private void updateInterfaceAfterSave() {
        informationScrollView.setVisibility(View.GONE);
        gridLayoutCalendar.setVisibility(View.VISIBLE);
        savedEventsLayout.setVisibility(View.VISIBLE);

        // Torna os botões e o TextView do mês visíveis novamente
        buttonPreviousMonth.setVisibility(View.VISIBLE);
        buttonNextMonth.setVisibility(View.VISIBLE);
        monthTextView.setVisibility(View.VISIBLE);
    }


    // Atualizar o método setupObservers para incluir a atualização de cores
    private void setupObservers() {
        plannerViewModel.getEvents().observe(this, events -> {
            updateSavedEvents(events);
            updateCalendarDayColors(events);
        });
    }

    // Método para atualizar as cores dos dias no calendário
    private void updateCalendarDayColors(List<Planner> events) {
        dayColors.clear(); // Limpa as cores anteriores
        for (Planner event : events) {
            String[] eventDateParts = event.getDay().split("-");
            int eventDay = Integer.parseInt(eventDateParts[0]);
            int eventMonth = Integer.parseInt(eventDateParts[1]);
            int eventYear = Integer.parseInt(eventDateParts[2]);

            if (eventMonth == currentMonth && eventYear == currentYear) {
                String dayKey = getDayKey(eventDay);
                dayColors.put(dayKey, event.getColor()); // Associa a cor ao dia
            }
        }
        setupCalendar(); // Atualiza o calendário com as novas cores
    }

    private void updateSavedEvents(List<Planner> events) {
        savedEventsLayout.removeAllViews();

        // Mapeia os dias que possuem eventos
        Map<String, Boolean> daysWithEvents = new HashMap<>();

        for (Planner event : events) {
            String[] eventDateParts = event.getDay().split("-");
            int eventDay = Integer.parseInt(eventDateParts[0]);
            int eventMonth = Integer.parseInt(eventDateParts[1]);
            int eventYear = Integer.parseInt(eventDateParts[2]);

            if (eventMonth == currentMonth && eventYear == currentYear) {
                daysWithEvents.put(getDayKey(eventDay), true); // Marca o dia com evento

                // Configuração do layout do evento com fundo arredondado
                LinearLayout eventLayout = new LinearLayout(this);
                eventLayout.setOrientation(LinearLayout.HORIZONTAL);

                // Define as margens para o layout do evento
                LinearLayout.LayoutParams eventLayoutParams = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );
                eventLayoutParams.setMargins(0, 16, 0, 16); // Adiciona margem superior e inferior
                eventLayout.setLayoutParams(eventLayoutParams);

                // Define o fundo arredondado e aplica a cor do evento ao layout
                eventLayout.setBackgroundResource(R.drawable.ct_task_agenda);
                eventLayout.getBackground().setTint(event.getColor()); // Define a cor de fundo do evento

                TextView eventTextView = new TextView(this);
                eventTextView.setText(String.format("Evento: %s\nDia: %d\nHora: %s\nInformações: %s",
                        event.getEventName(), eventDay, event.getEventTime(), event.getAdditionalInfo()));
                eventTextView.setTextSize(16);
                eventTextView.setTextColor(Color.BLACK); // Cor do texto
                eventTextView.setLineSpacing(1, 1.2f);

                // Define as margens para o TextView
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        0, LinearLayout.LayoutParams.WRAP_CONTENT, 1);
                params.setMargins(16, 0, 0, 0); // Margem esquerda de 16 pixels
                eventTextView.setLayoutParams(params);

                // Aumenta o padding do TextView
                eventTextView.setPadding(16, 24, 16, 16); // Padding: (esquerda, cima, direita, baixo)

                // Cria um layout vertical para os ícones de exclusão e edição
                LinearLayout iconLayout = new LinearLayout(this);
                iconLayout.setOrientation(LinearLayout.VERTICAL);
                iconLayout.setLayoutParams(new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT));
                iconLayout.setPadding(16, 0, 0, 0);

                // Aumenta o padding do layout de ícones
                iconLayout.setPadding(16, 16, 16, 16); // Padding: (esquerda, cima, direita, baixo)

                ImageView deleteIcon = new ImageView(this);
                deleteIcon.setImageResource(R.drawable.ic_delete);
                deleteIcon.setLayoutParams(new LinearLayout.LayoutParams(80, 80));
                deleteIcon.setOnClickListener(v -> {
                    plannerViewModel.removeEvent(event);
                    Toast.makeText(this, "Evento removido", Toast.LENGTH_SHORT).show();
                    updateCalendarDayColor(event.getDay());
                    updateSavedEvents(plannerViewModel.getEvents().getValue()); // Atualiza a lista de eventos
                });


                // Adiciona os ícones ao layout vertical
                iconLayout.addView(deleteIcon);

                // Adiciona o TextView e o layout dos ícones ao layout principal
                eventLayout.addView(eventTextView);
                eventLayout.addView(iconLayout);
                savedEventsLayout.addView(eventLayout);
            }
        }

        // Atualiza as cores dos dias no calendário
        for (int i = 0; i < gridLayoutCalendar.getChildCount(); i++) {
            TextView dayTextView = (TextView) gridLayoutCalendar.getChildAt(i);
            String dayKey = getDayKey(Integer.parseInt(dayTextView.getText().toString()));

            // Se o dia não tiver evento, define a cor como branco
            if (!daysWithEvents.containsKey(dayKey)) {
                dayColors.put(dayKey, Color.WHITE);
            }

            dayTextView.setBackgroundColor(dayColors.getOrDefault(dayKey, Color.WHITE));
        }
    }


    private void updateCalendarDayColor(String dayKey) {
        dayColors.put(dayKey, Color.WHITE); // Define a cor como branco ao remover o evento


        for (int i = 0; i < gridLayoutCalendar.getChildCount(); i++) {
            TextView dayTextView = (TextView) gridLayoutCalendar.getChildAt(i);
            String currentDayKey = getDayKey(Integer.parseInt(dayTextView.getText().toString()));
            dayTextView.setBackgroundColor(dayColors.getOrDefault(currentDayKey, Color.WHITE));
        }
    }


    private String getDayKey(int day) {
        return day + "-" + currentMonth + "-" + currentYear;
    }

/*
    private void editEvent(Planner event) {
        currentEditingEvent = event; // Armazena o evento que está sendo editado
        String[] eventDateParts = event.getDay().split("-");
        int eventDay = Integer.parseInt(eventDateParts[0]);
        int eventMonth = Integer.parseInt(eventDateParts[1]);
        int eventYear = Integer.parseInt(eventDateParts[2]);

        // Verifica se o mês e o ano do evento são iguais ao mês e ano atuais
        if (eventMonth == currentMonth && eventYear == currentYear) {
            // Seleciona o dia do evento no calendário
            for (int i = 0; i < gridLayoutCalendar.getChildCount(); i++) {
                TextView dayTextView = (TextView) gridLayoutCalendar.getChildAt(i);
                int dayInCalendar = Integer.parseInt(dayTextView.getText().toString());

                // Se for o dia do evento, atualiza o fundo para indicar que está selecionado
                if (dayInCalendar == eventDay) {
                    dayTextView.setBackgroundColor(Color.parseColor("#FFEFDE")); // Cor de destaque para o dia selecionado
                    selectedDayTextView = dayTextView;  // Marca o TextView como selecionado
                    break;
                }
            }

            // Preenche os campos do formulário com os detalhes do evento
            editTextEventName.setText(event.getEventName());
            textViewEventTime.setText(event.getEventTime());
            editTextAdditionalInfo.setText(event.getAdditionalInfo());
            plannerViewModel.setColor(event.getColor());

            informationScrollView.setVisibility(View.VISIBLE);
            gridLayoutCalendar.setVisibility(View.GONE);
            savedEventsLayout.setVisibility(View.GONE);

            // Esconde os botões e o TextView do mês
            buttonPreviousMonth.setVisibility(View.GONE);
            buttonNextMonth.setVisibility(View.GONE);
            monthTextView.setVisibility(View.GONE);
        } else {
            Toast.makeText(this, "O evento pertence a outro mês/ano. Mude para o mês correto para editá-lo.", Toast.LENGTH_LONG).show();
        }
    }*/

}
