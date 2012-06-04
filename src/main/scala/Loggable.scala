import org.slf4j.{Logger, LoggerFactory}

trait Loggable {
    private val log: Logger = LoggerFactory.getLogger( getClass )

    class Log( f: ( String ) => Unit, f2: ( String, Throwable ) => Unit ) {
        def << ( what: Any ) {
            what match {
                case msg: String => f( "string " + msg )
                case msg: Tuple1[ String ] => f( "tuple " + msg._1 )
                case msg: (String, Throwable) => f2( "tuple " + msg._1, msg._2 )
                case msg: Tuple1[ Any ] => f( msg._1.toString )
                case something => f( something.toString )
            }


        }
    }

    val INFO = new Log( log.info( _ ), log.info( _, _ ) )

    val ERROR = new Log( log.error( _ ), log.error( _, _ ) )

}
