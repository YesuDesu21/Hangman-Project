# Python stubs generated by omniidl from LeaderboardService.idl
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
_0_compilations = omniORB.openModule("compilations", r"LeaderboardService.idl")
_0_compilations__POA = omniORB.openModule("compilations__POA", r"LeaderboardService.idl")


# struct PlayerScore
_0_compilations.PlayerScore = omniORB.newEmptyClass()
class PlayerScore (omniORB.StructBase):
    _NP_RepositoryId = "IDL:compilations/PlayerScore:1.0"

    def __init__(self, username, gamesWon):
        self.username = username
        self.gamesWon = gamesWon

_0_compilations.PlayerScore = PlayerScore
_0_compilations._d_PlayerScore  = (omniORB.tcInternal.tv_struct, PlayerScore, PlayerScore._NP_RepositoryId, "PlayerScore", "username", (omniORB.tcInternal.tv_string,0), "gamesWon", omniORB.tcInternal.tv_short)
_0_compilations._tc_PlayerScore = omniORB.tcInternal.createTypeCode(_0_compilations._d_PlayerScore)
omniORB.registerType(PlayerScore._NP_RepositoryId, _0_compilations._d_PlayerScore, _0_compilations._tc_PlayerScore)
del PlayerScore

# typedef ... PlayerScoreSeq
class PlayerScoreSeq:
    _NP_RepositoryId = "IDL:compilations/PlayerScoreSeq:1.0"
    def __init__(self, *args, **kw):
        raise RuntimeError("Cannot construct objects of this type.")
_0_compilations.PlayerScoreSeq = PlayerScoreSeq
_0_compilations._d_PlayerScoreSeq  = (omniORB.tcInternal.tv_sequence, omniORB.typeMapping["IDL:compilations/PlayerScore:1.0"], 0)
_0_compilations._ad_PlayerScoreSeq = (omniORB.tcInternal.tv_alias, PlayerScoreSeq._NP_RepositoryId, "PlayerScoreSeq", (omniORB.tcInternal.tv_sequence, omniORB.typeMapping["IDL:compilations/PlayerScore:1.0"], 0))
_0_compilations._tc_PlayerScoreSeq = omniORB.tcInternal.createTypeCode(_0_compilations._ad_PlayerScoreSeq)
omniORB.registerType(PlayerScoreSeq._NP_RepositoryId, _0_compilations._ad_PlayerScoreSeq, _0_compilations._tc_PlayerScoreSeq)
del PlayerScoreSeq

# interface LeaderboardService
_0_compilations._d_LeaderboardService = (omniORB.tcInternal.tv_objref, "IDL:compilations/LeaderboardService:1.0", "LeaderboardService")
omniORB.typeMapping["IDL:compilations/LeaderboardService:1.0"] = _0_compilations._d_LeaderboardService
_0_compilations.LeaderboardService = omniORB.newEmptyClass()
class LeaderboardService :
    _NP_RepositoryId = _0_compilations._d_LeaderboardService[1]

    def __init__(self, *args, **kw):
        raise RuntimeError("Cannot construct objects of this type.")

    _nil = CORBA.Object._nil


_0_compilations.LeaderboardService = LeaderboardService
_0_compilations._tc_LeaderboardService = omniORB.tcInternal.createTypeCode(_0_compilations._d_LeaderboardService)
omniORB.registerType(LeaderboardService._NP_RepositoryId, _0_compilations._d_LeaderboardService, _0_compilations._tc_LeaderboardService)

# LeaderboardService operations and attributes
LeaderboardService._d_getTopPlayers = ((), (omniORB.typeMapping["IDL:compilations/PlayerScoreSeq:1.0"], ), None)
LeaderboardService._d_recordPlayerWin = (((omniORB.tcInternal.tv_string,0), ), (), None)

# LeaderboardService object reference
class _objref_LeaderboardService (CORBA.Object):
    _NP_RepositoryId = LeaderboardService._NP_RepositoryId

    def __init__(self, obj):
        CORBA.Object.__init__(self, obj)

    def getTopPlayers(self, *args):
        return self._obj.invoke("getTopPlayers", _0_compilations.LeaderboardService._d_getTopPlayers, args)

    def recordPlayerWin(self, *args):
        return self._obj.invoke("recordPlayerWin", _0_compilations.LeaderboardService._d_recordPlayerWin, args)

omniORB.registerObjref(LeaderboardService._NP_RepositoryId, _objref_LeaderboardService)
_0_compilations._objref_LeaderboardService = _objref_LeaderboardService
del LeaderboardService, _objref_LeaderboardService

# LeaderboardService skeleton
__name__ = "compilations__POA"
class LeaderboardService (PortableServer.Servant):
    _NP_RepositoryId = _0_compilations.LeaderboardService._NP_RepositoryId


    _omni_op_d = {"getTopPlayers": _0_compilations.LeaderboardService._d_getTopPlayers, "recordPlayerWin": _0_compilations.LeaderboardService._d_recordPlayerWin}

LeaderboardService._omni_skeleton = LeaderboardService
_0_compilations__POA.LeaderboardService = LeaderboardService
omniORB.registerSkeleton(LeaderboardService._NP_RepositoryId, LeaderboardService)
del LeaderboardService
__name__ = "compilations"

#
# End of module "compilations"
#
__name__ = "LeaderboardService_idl"

_exported_modules = ( "compilations", )

# The end.
