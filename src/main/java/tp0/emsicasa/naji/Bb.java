    package tp0.emsicasa.naji;

    import jakarta.faces.application.FacesMessage;
    import jakarta.faces.context.FacesContext;
    import jakarta.faces.model.SelectItem;
    import jakarta.faces.view.ViewScoped;
    import jakarta.inject.Inject;
    import jakarta.inject.Named;

    import java.io.Serializable;
    import java.util.*;

    /**
     * Backing bean pour la page JSF index.xhtml.
     * Portée view pour conserver l'état de la conversation pendant plusieurs requêtes HTTP.
     */
    @Named
    @ViewScoped
    public class Bb implements Serializable {

        private String systemRole;
        private boolean systemRoleChangeable = true;
        private String question;
        private String reponse;
        private StringBuilder conversation = new StringBuilder();

        @Inject
        private FacesContext facesContext;

        public Bb() {}

        public String getSystemRole() {
            return systemRole;
        }

        public void setSystemRole(String systemRole) {
            this.systemRole = systemRole;
        }

        public boolean isSystemRoleChangeable() {
            return systemRoleChangeable;
        }

        public String getQuestion() {
            return question;
        }

        public void setQuestion(String question) {
            this.question = question;
        }

        public String getReponse() {
            return reponse;
        }

        public void setReponse(String reponse) {
            this.reponse = reponse;
        }

        public String getConversation() {
            return conversation.toString();
        }

        public void setConversation(String conversation) {
            this.conversation = new StringBuilder(conversation);
        }

        /**
         * Envoie la question au serveur.
         * Le traitement consiste à inverser les mots de la question et à afficher des statistiques
         * sur le nombre de mots et de caractères.
         *
         * @return null pour rester sur la même page.
         */
        public String envoyer() {
            if (question == null || question.isBlank()) {
                FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                        "Texte question vide", "Il manque le texte de la question");
                facesContext.addMessage(null, message);
                return null;
            }

            // Analyse de la fréquence des mots
            String[] mots = question.toLowerCase(Locale.FRENCH).split("\\s+");
            Map<String, Integer> frequences = new HashMap<>();
            for (String mot : mots) {
                frequences.put(mot, frequences.getOrDefault(mot, 0) + 1);
            }

            StringBuilder analyseFrequence = new StringBuilder("Fréquence des mots :\n");
            for (Map.Entry<String, Integer> entree : frequences.entrySet()) {
                analyseFrequence.append("- ").append(entree.getKey()).append(" : ").append(entree.getValue()).append("\n");
            }

            // Génération de texte aléatoire
            List<String> listeMots = Arrays.asList(mots);
            Collections.shuffle(listeMots);
            String texteAleatoire = "Texte généré aléatoirement : " + String.join(" ", listeMots);

            // Construire la réponse
            this.reponse = analyseFrequence.toString() + "\n" + texteAleatoire;

            // Ajouter le rôle système au début si c'est la première interaction
            if (this.conversation.isEmpty()) {
                this.reponse = systemRole.toUpperCase(Locale.FRENCH) + "\n" + this.reponse;
                this.systemRoleChangeable = false;
            }

            // Mettre à jour la conversation
            afficherConversation();

            return null;
        }

        public String nouveauChat() {
            return "index";
        }

        private void afficherConversation() {
            this.conversation.append("== User:\n").append(question).append("\n== Serveur:\n").append(reponse).append("\n");
        }

        public List<SelectItem> getSystemRoles() {
            List<SelectItem> listeSystemRoles = new ArrayList<>();
            String role = """
                    You are a helpful assistant. You help the user to find the information they need.
                    If the user type a question, you answer it.
                    """;
            listeSystemRoles.add(new SelectItem(role, "Assistant"));
            role = """
                    You are an interpreter. You translate from English to French and from French to English.
                    If the user type a French text, you translate it into English.
                    If the user type an English text, you translate it into French.
                    If the text contains only one to three words, give some examples of usage of these words in English.
                    """;
            listeSystemRoles.add(new SelectItem(role, "Traducteur Anglais-Français"));
            role = """
                    You are a travel guide. If the user type the name of a country or of a town,
                    you tell them what are the main places to visit in the country or the town
                    are you tell them the average price of a meal.
                    """;
            listeSystemRoles.add(new SelectItem(role, "Guide touristique"));
            this.systemRole = (String) listeSystemRoles.get(0).getValue();
            return listeSystemRoles;
        }
    }
