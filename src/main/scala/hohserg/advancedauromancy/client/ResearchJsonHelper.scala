package hohserg.advancedauromancy.client

import java.io.{File, PrintWriter}

import com.google.gson.GsonBuilder
import thaumcraft.api.research.ResearchEntry

object ResearchJsonHelper {
  def load(cat: String):String = scala.io.Source.fromFile(new File("./"+cat)).getLines().mkString("\n")

  def save(cat: String, value: String): Unit = new PrintWriter("./"+cat) { write(value); close() }

  def updateEntry(re: ResearchEntry):Unit = {
    val cat=re.getCategory+".json"
    val gson = new GsonBuilder().setPrettyPrinting().create
    val rj:ResearchJson=gson.fromJson(load(cat),classOf[ResearchJson])
    val rj2 = rj.copy(entries = rj.entries.map(rej=>
      if(rej.key==re.getKey)
        rej.copy(location = rej.location.updated(0,re.getDisplayColumn).updated(1,re.getDisplayRow))
      else
        rej
    ))
    save(cat,gson.toJson(rj2))

  }

  case class ResearchJson(entries:Array[ResearchEntryJson]){

  }
  case class ResearchEntryJson(
                                key:String,
                                name:String,
                                icons:Array[String],
                                category:String,
                                location:Array[Int],
                                parents:Array[String],
                                siblings:Array[String],
                                meta:Array[String],
                                stages:Array[StageJson]
                              )
  case class StageJson(text:String,required_craft:Array[String],recipes:Array[String])

}
