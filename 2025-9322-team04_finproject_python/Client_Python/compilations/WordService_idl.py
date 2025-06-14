# Python stubs generated by omniidl from WordService.idl
# DO NOT EDIT THIS FILE!

import omniORB, _omnipy
from omniORB import CORBA, PortableServer
_0_CORBA = CORBA


_omnipy.checkVersion(4,2, __file__, 1)

try:
    property
except NameError:
    def property(*args):
        return None


#
# Start of module "compilations"
#
__name__ = "compilations"
_0_compilations = omniORB.openModule("compilations", r"WordService.idl")
_0_compilations__POA = omniORB.openModule("compilations__POA", r"WordService.idl")


# interface WordService
_0_compilations._d_WordService = (omniORB.tcInternal.tv_objref, "IDL:compilations/WordService:1.0", "WordService")
omniORB.typeMapping["IDL:compilations/WordService:1.0"] = _0_compilations._d_WordService
_0_compilations.WordService = omniORB.newEmptyClass()
class WordService :
    _NP_RepositoryId = _0_compilations._d_WordService[1]

    def __init__(self, *args, **kw):
        raise RuntimeError("Cannot construct objects of this type.")

    _nil = CORBA.Object._nil

    
    # exception GameNotFoundException
    _0_compilations.WordService.GameNotFoundException = omniORB.newEmptyClass()
    class GameNotFoundException (CORBA.UserException):
        _NP_RepositoryId = "IDL:compilations/WordService/GameNotFoundException:1.0"

        _NP_ClassName = "compilations.WordService.GameNotFoundException"

        def __init__(self):
            CORBA.UserException.__init__(self)
    
    _d_GameNotFoundException  = (omniORB.tcInternal.tv_except, GameNotFoundException, GameNotFoundException._NP_RepositoryId, "GameNotFoundException")
    _tc_GameNotFoundException = omniORB.tcInternal.createTypeCode(_d_GameNotFoundException)
    omniORB.registerType(GameNotFoundException._NP_RepositoryId, _d_GameNotFoundException, _tc_GameNotFoundException)


_0_compilations.WordService = WordService
_0_compilations._tc_WordService = omniORB.tcInternal.createTypeCode(_0_compilations._d_WordService)
omniORB.registerType(WordService._NP_RepositoryId, _0_compilations._d_WordService, _0_compilations._tc_WordService)

# WordService operations and attributes
WordService._d_getNewWord = (((omniORB.tcInternal.tv_string,0), ), ((omniORB.tcInternal.tv_string,0), ), None)
WordService._d_markWordAsUsed = (((omniORB.tcInternal.tv_string,0), (omniORB.tcInternal.tv_string,0)), (), None)
WordService._d_getCurrentWord = (((omniORB.tcInternal.tv_string,0), ), ((omniORB.tcInternal.tv_string,0), ), None)
WordService._d_clearCurrentWord = (((omniORB.tcInternal.tv_string,0), ), (), None)
WordService._d_getRoundWord = (((omniORB.tcInternal.tv_string,0), (omniORB.tcInternal.tv_string,0)), ((omniORB.tcInternal.tv_string,0), ), {_0_compilations.WordService.GameNotFoundException._NP_RepositoryId: _0_compilations.WordService._d_GameNotFoundException})

# WordService object reference
class _objref_WordService (CORBA.Object):
    _NP_RepositoryId = WordService._NP_RepositoryId

    def __init__(self, obj):
        CORBA.Object.__init__(self, obj)

    def getNewWord(self, *args):
        return self._obj.invoke("getNewWord", _0_compilations.WordService._d_getNewWord, args)

    def markWordAsUsed(self, *args):
        return self._obj.invoke("markWordAsUsed", _0_compilations.WordService._d_markWordAsUsed, args)

    def getCurrentWord(self, *args):
        return self._obj.invoke("getCurrentWord", _0_compilations.WordService._d_getCurrentWord, args)

    def clearCurrentWord(self, *args):
        return self._obj.invoke("clearCurrentWord", _0_compilations.WordService._d_clearCurrentWord, args)

    def getRoundWord(self, *args):
        return self._obj.invoke("getRoundWord", _0_compilations.WordService._d_getRoundWord, args)

omniORB.registerObjref(WordService._NP_RepositoryId, _objref_WordService)
_0_compilations._objref_WordService = _objref_WordService
del WordService, _objref_WordService

# WordService skeleton
__name__ = "compilations__POA"
class WordService (PortableServer.Servant):
    _NP_RepositoryId = _0_compilations.WordService._NP_RepositoryId


    _omni_op_d = {"getNewWord": _0_compilations.WordService._d_getNewWord, "markWordAsUsed": _0_compilations.WordService._d_markWordAsUsed, "getCurrentWord": _0_compilations.WordService._d_getCurrentWord, "clearCurrentWord": _0_compilations.WordService._d_clearCurrentWord, "getRoundWord": _0_compilations.WordService._d_getRoundWord}

WordService._omni_skeleton = WordService
_0_compilations__POA.WordService = WordService
omniORB.registerSkeleton(WordService._NP_RepositoryId, WordService)
del WordService
__name__ = "compilations"

#
# End of module "compilations"
#
__name__ = "WordService_idl"

_exported_modules = ( "compilations", )

# The end.
