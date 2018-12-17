package systems.mobile.vildmad.find_fragment;

public class PlantItem {



        private String plantName;
        private String imagePath;
        private boolean checked = false;




        public PlantItem(String plantName, String imagePath) {
            this.plantName = plantName;
            this.imagePath = imagePath;
        }


        public String getplantName() {
            return plantName;
        }

        public String getimagePath() {
            return imagePath;
        }

        public void setplantName(String plantName) {
            this.plantName = plantName;
        }

        public void setimagePath(String imagePath) {
            this.imagePath = imagePath;
        }

        public boolean isChecked() {
            return checked;
        }

        public void setChecked(boolean checked) {
            this.checked = checked;
        }


}
