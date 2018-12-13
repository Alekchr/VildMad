package systems.mobile.vildmad.find_fragment;

public class PlantItem {



        private String plantName;
        private String imagePath;

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


}
